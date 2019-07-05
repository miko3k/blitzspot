package org.deletethis.blitzspot.app.activities.mycroft;

import android.os.Handler;
import android.webkit.JavascriptInterface;

import org.deletethis.blitzspot.lib.Logging;

import java.util.function.Consumer;


class WebViewCallback {
    private final Handler handler;
    private final Consumer<String> addEngine;

    public WebViewCallback(Handler handler, Consumer<String> addEngine) {
        this.handler = handler;
        this.addEngine = addEngine;
    }

    @JavascriptInterface
    public void add(String url) {
        Logging.MYCROFT.i("add open search: " + url  + ", Thread: " + Thread.currentThread().getName());

        handler.post(() -> addEngine.accept(url));
    }
 }