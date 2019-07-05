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

package org.deletethis.blitzspot.app.activities.mycroft;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.common.io.CharStreams;


import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.lib.Logging;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

class WebClient extends WebViewClient {

    public static class Params {
        private Consumer<String> urlConsumer;
        private Consumer<Uri> foreginUrlConsumer;

        public void setUrlConsumer(Consumer<String> urlConsumer) {
            this.urlConsumer = urlConsumer;
        }

        public void setForeginUrlConsumer(Consumer<Uri> foreginUrlConsumer) {
            this.foreginUrlConsumer = foreginUrlConsumer;
        }
    }

    private final Context context;
    private final Params params;
    private final String javascript;
    private final boolean cssEnabled = false;

    public WebClient(Context context, Params params) {
        this.context = context;
        this.params = params;
        try(Reader inputStream = new InputStreamReader(context.getResources().openRawResource(R.raw.mycroft_script), StandardCharsets.UTF_8)) {
            javascript = CharStreams.toString(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean shouldOverrideUrlLoading (WebView view,
                                             WebResourceRequest request) {
        Uri url = request.getUrl();
        Logging.MYCROFT.i("shouldOverrideUrlLoading: " + url + ", Thread: " + Thread.currentThread().getName());

        boolean isMine = "mycroftproject.com".equalsIgnoreCase(url.getHost());
        if(isMine) {
            return false;
        } else {
            params.foreginUrlConsumer.accept(url);
            return true;
        }
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        Uri url = request.getUrl();
        String path = url.getPath();
        if(path != null && path.endsWith("mycroft.css") && cssEnabled) {
            Logging.MYCROFT.i("intercepting resource: " + url + ", Thread: " + Thread.currentThread().getName());
            return new WebResourceResponse(
                    "text/css",
                    "utf-8",
                    200,
                    "Ok",
                    null,
                    context.getResources().openRawResource(R.raw.mycroft_style));
        }

        return null;
    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
        view.evaluateJavascript(javascript, null);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if(params.urlConsumer != null)
            params.urlConsumer.accept(url);
    }
}
