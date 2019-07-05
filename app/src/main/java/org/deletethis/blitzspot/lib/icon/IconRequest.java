/*
 * blitzspot
 * Copyright (C) 2018-2019 Peter Hanula
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.deletethis.blitzspot.lib.icon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.mejico.IconFormatException;
import org.deletethis.mejico.android.IconParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class IconRequest extends Request<ScaledBitmap> {
    private static final Object LOCK = new Object();

    private final String userAgent;
    private final int wantedSize;
    private final PluginIconLoaderCallback callback;
    private final static long DAY_MS = 1000L * 3600L * 24L;
    private final static long MIN_TTL = DAY_MS * 31;
    private final static long MIN_SOFT_TTL = DAY_MS;

    IconRequest(String url,
                       String userAgent,
                       int wantedSize,
                       PluginIconLoaderCallback callback) {

        super(Method.GET, url, error -> {
            NetworkResponse resp = error.networkResponse;
            String debug;
            if(resp == null) {
                debug = "Null response";
            } else {
                debug = "Data len: " + resp.data.length + ", data: " + new String(resp.data, StandardCharsets.ISO_8859_1);
            }
            callback.iconRetrievalFailed(url, error, debug);
        });
        this.userAgent = userAgent;
        this.wantedSize = wantedSize;
        this.callback = callback;
    }

    @Override
    protected void deliverResponse(ScaledBitmap response) {
        callback.iconRetrieved(getUrl(), response);
    }

    @Override
    public Map<String, String> getHeaders(){

        Map<String, String> headers = new HashMap<>();
        headers.put("User-agent", userAgent);
        return headers;
    }

    private Bitmap createSmallIcon(Bitmap bitmap, int wantedW, int wantedH) {
        Bitmap result = Bitmap.createBitmap(wantedW, wantedH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Paint gray = new Paint();
        gray.setColor(0x22000000);
        gray.setStyle(Paint.Style.FILL);

        canvas.drawOval(0, 0, wantedW-1, wantedH-1, gray);

        bitmap = Bitmap.createScaledBitmap(bitmap, wantedW/2, wantedH/2, true);
        //noinspection IntegerDivisionInFloatingPointContext: we don't like sub-pixel drawing. Not pretty
        canvas.drawBitmap(bitmap, wantedW/4, wantedH/4, null);

        return result;
    }

    private ScaledBitmap createScaledBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int size = w>h ? w : h;

        if(size != wantedSize) {
            int scaledW = w * wantedSize / size;
            int scaledH = h * wantedSize / size;

            if(size * 3 < wantedSize) {
                bitmap = createSmallIcon(bitmap, scaledW, scaledH);
            } else {
                bitmap = Bitmap.createScaledBitmap(bitmap, scaledW, scaledH, true);
            }
        }

        return new ScaledBitmap(bitmap, w, h);
    }

    private Cache.Entry parseCacheHeaders(NetworkResponse response) {
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        /*LibLogging.LIB.d("created cache entry: " +
                "etag=" + entry.etag + ", " +
                "serverDate=" + new Date(entry.serverDate) + ", " +
                "lastModified=" + new Date(entry.lastModified) + ", " +
                "ttl=" + new Date(entry.ttl) + ", " +
                "softTtl=" + new Date(entry.softTtl));*/

        // force more aggressive caching, these are just icons after all
        long now = System.currentTimeMillis();
        if(entry.ttl < now + MIN_TTL) {
            entry.ttl = now + MIN_TTL;
        }
        if(entry.softTtl < now + MIN_SOFT_TTL) {
            entry.softTtl = now + MIN_SOFT_TTL;
        }
        return entry;
    }

    private Response<ScaledBitmap> doParse(NetworkResponse response) {
        try {
            List<Bitmap> icons = IconParser.getInstance().getIcons(response.data);
            if (icons.isEmpty()) {
                return Response.error(new ParseError(response));
            }
            BestSize<Bitmap> best = BestSize.bitmaps(wantedSize);
            best.addAll(icons);

            ScaledBitmap scaledBitmap = createScaledBitmap(best.getBestOrThrow());
            return Response.success(scaledBitmap, parseCacheHeaders(response));
        } catch (IconFormatException ex) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(response.data, 0, response.data.length);
            if (bitmap == null) {
                return Response.error(new VolleyError("Unable to parse bitmap"));
            }
            ScaledBitmap scaledBitmap = createScaledBitmap(bitmap);
            return Response.success(scaledBitmap, parseCacheHeaders(response));
        } catch (IOException ex) {
            Logging.ICON.e("unable to parse icon", ex);
            return Response.error(new ParseError(ex));
        }
    }

    @Override
    protected Response<ScaledBitmap> parseNetworkResponse(NetworkResponse response) {
        // no particular reason for a global lock, but volley seems to be doing
        // something in image loader ... comment says that image scaling might be expensive.
        // who knows but I do not really care
        synchronized (LOCK) {
            return doParse(response);
        }
    }
}