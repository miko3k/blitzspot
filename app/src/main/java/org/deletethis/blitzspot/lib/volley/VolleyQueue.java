package org.deletethis.blitzspot.lib.volley;

import com.android.volley.Request;

public interface VolleyQueue {
    <T> void add(Request<T> req, Object tag);
    void cancel(Object tag);
}
