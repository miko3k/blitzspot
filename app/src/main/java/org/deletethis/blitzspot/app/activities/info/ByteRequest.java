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

package org.deletethis.blitzspot.app.activities.info;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

class ByteRequest extends Request<byte[]> {
    private final Response.Listener<byte[]> mListener;

    /**
     * Creates a new GET request.
     *
     * @param url URL to fetch the string at
     * @param listener Listener to receive the byte array response
     */
    ByteRequest(String url, Response.Listener<byte[]> listener,
                       Response.ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url URL to fetch the byte array at
     * @param listener Listener to receive the byte array response or error message
     */
    private ByteRequest(int method, String url, Response.Listener<byte[]> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    @Override
    protected void deliverResponse(byte[] response) {
        if(null != mListener){
            mListener.onResponse(response);
        }
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    public String getBodyContentType() {
        return "application/octet-stream";
    }
}