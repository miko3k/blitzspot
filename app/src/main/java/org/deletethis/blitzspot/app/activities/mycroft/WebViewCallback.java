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