package org.deletethis.blitzspot.lib.volley;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

public class VolleySingleton implements VolleyQueue {
    private static VolleySingleton mInstance;
    private final RequestQueue mRequestQueue;

    private VolleySingleton(Context context) {
        VolleyLog.DEBUG = true;

        Cache cache = new DiskBasedCache(context.getCacheDir(), 2 * 1024 * 1024); // 2MB cap
        //Cache cache = new NoCache();

        Network network = new BasicNetwork(new HurlStack());

        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new VolleySingleton(context.getApplicationContext());
        }
        return mInstance;
    }

    public <T> void add(Request<T> req, Object tag) {
        if(tag != null)
            req.setTag(tag);

        mRequestQueue.add(req);
    }


    public void cancel(Object tag) {
        mRequestQueue.cancelAll(tag);
    }
}