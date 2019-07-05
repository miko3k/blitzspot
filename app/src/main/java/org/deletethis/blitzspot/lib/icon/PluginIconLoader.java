package org.deletethis.blitzspot.lib.icon;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebSettings;

import com.android.volley.Request;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.lib.volley.VolleyQueue;
import org.deletethis.blitzspot.lib.volley.VolleySingleton;
import org.deletethis.search.parser.UrlIconAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

class PluginIconLoader implements PluginIconLoaderCallback {
    private static PluginIconLoader instance;

    private final VolleyQueue volley;
    private final String userAgent;
    private final int wantedSize;

    private final Map<String, RemoteImage> liveSources = new HashMap<>();
    private final Map<UrlIconAddress, LiveData<Bitmap>> liveIcons = new HashMap<>();

    private PluginIconLoader(Context context) {
        this.userAgent = WebSettings.getDefaultUserAgent(context);
        this.wantedSize = (int)context.getResources().getDimension(R.dimen.pluginIconSize);
        this.volley = VolleySingleton.getInstance(context);
    }

    private LiveData<Bitmap> createLiveData(UrlIconAddress icon) {
        MediatorLiveData<Bitmap> src = new MediatorLiveData<>();
        List<LiveData<ScaledBitmap>> sourceLiveData = new ArrayList<>();

        if(icon.isEmpty()) {
            throw new IllegalArgumentException();
        }

        for(String url: icon) {
            RemoteImage remoteImage = liveSources.get(url);
            if(remoteImage == null) {
                remoteImage = new RemoteImage(url);
                liveSources.put(url, remoteImage);
            }
            sourceLiveData.add(remoteImage.getLiveData());
        }

        Observer<ScaledBitmap> observer = unused -> {
            BestSize<ScaledBitmap> iconsIHave = BestSize.icons(wantedSize);
            for(LiveData<ScaledBitmap> x: sourceLiveData) {
                ScaledBitmap value = x.getValue();
                if(value != null) {
                    iconsIHave.add(value);
                }
            }
            Bitmap best;
            if(iconsIHave.isEmpty()) {
                best = null;
            } else {
                best = iconsIHave.getBestOrThrow().getScaledBitmap();
            }
            Bitmap current = src.getValue();
            if(best != current)
                src.setValue(best);
        };

        for(LiveData<ScaledBitmap> iconLiveData: sourceLiveData) {
            src.addSource(iconLiveData, observer);
        }
        return src;
    }

    private long now() {
        return System.currentTimeMillis();
    }

    private LiveData<Bitmap> getOrCreateLiveData(UrlIconAddress icon) {
        LiveData<Bitmap> bitmapLiveData = liveIcons.get(icon);
        if(bitmapLiveData == null) {
            bitmapLiveData = createLiveData(icon);
            liveIcons.put(icon, bitmapLiveData);
        }
        return bitmapLiveData;
    }

    public LiveData<Bitmap> getIconBitmap(UrlIconAddress icon) {
        LiveData<Bitmap> result = getOrCreateLiveData(icon);
        long now = now();
        for(RemoteImage ri: liveSources.values()) {
            if(ri.needLoad(now)) {
                Request<?> request = new IconRequest(ri.getUrl(), userAgent, wantedSize, this);
                ri.setStatus(RemoteImage.Status.LOADING);
                volley.add(request, null);
            }

        }

        return result;
    }

    static PluginIconLoader getInstance(Context context) {
        if(instance == null) {
            instance = new PluginIconLoader(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void iconRetrieved(String url, ScaledBitmap scaledBitmap) {
        RemoteImage remoteImage = liveSources.get(url);
        if(remoteImage != null) {
            remoteImage.setStatus(RemoteImage.Status.LOADED);
            remoteImage.getLiveData().setValue(scaledBitmap);
            remoteImage.setLastLoadTimestamp(now());
        }
    }

    @Override
    public void iconRetrievalFailed(String url, Throwable error, String debug) {
        RemoteImage remoteImage = liveSources.get(url);
        if(remoteImage != null) {
            remoteImage.setStatus(RemoteImage.Status.FAILED);
            remoteImage.setLastLoadTimestamp(now());
        }
    }
}
