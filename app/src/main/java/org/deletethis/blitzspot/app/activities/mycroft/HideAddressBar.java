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
import androidx.core.view.MotionEventCompat;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import org.deletethis.blitzspot.lib.Logging;


public class HideAddressBar implements View.OnTouchListener, Runnable {
    private final View toolbar;
    private final WebView webView;
    private final int speed;
    private final Handler handler;

    private final static int DELAY = 30;

    public HideAddressBar(Handler handler, View toolbar, WebView webView, float speed) {
        this.handler = handler;
        this.toolbar = toolbar;
        this.webView = webView;
        this.speed = (int)speed;
    }

    private float lastYAbs;

    private class LayoutInfo {
        private final RelativeLayout.LayoutParams layout;
        private final int height;
        // represents how much of title bar is visible
        private int current;

        LayoutInfo() {
            this.layout = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
            this.height = toolbar.getHeight();
            // represents how much of title bar is visible
            this.current = height + layout.topMargin;
        }

        void checkAndUpdate(int next) {
            if(next > height) {
                next = height;
            }
            if(next < 0) {
                next = 0;
            }

            if(current != next) {
                if(next == 0) {
                    toolbar.setVisibility(View.INVISIBLE);
                } else if(current == 0){
                    toolbar.setVisibility(View.VISIBLE);
                }

                this.current = next;
                layout.topMargin = current - height;
                toolbar.setLayoutParams(layout);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if(!MotionEventCompat.isFromSource(event, InputDevice.SOURCE_TOUCHSCREEN)) {
            return false;
        }

        handler.removeCallbacks(this);

        LayoutInfo layoutInfo = new LayoutInfo();
        int previous = layoutInfo.current;
        float yAbs = event.getY() + previous;

        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            float dy = yAbs - lastYAbs;

            int next = previous + (int)dy;

            Logging.MYCROFT.d(", current: " + previous + ", next: " + next + ", event: " + event);
            layoutInfo.checkAndUpdate(next);
            event.setLocation(event.getX(), event.getY() + (previous - layoutInfo.current));
        }
        if(event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
            postUpdateIfNeeded(layoutInfo);
        }

        lastYAbs = yAbs;
        return false;
    }

    private void postUpdateIfNeeded(LayoutInfo layoutInfo) {
        if(layoutInfo.current == 0 || layoutInfo.current == layoutInfo.height)
            return;

        handler.postDelayed(this, DELAY);
    }

    @Override
    public void run() {
        LayoutInfo layoutInfo = new LayoutInfo();

        int next;
        if(layoutInfo.current < layoutInfo.height / 2) {
            next = layoutInfo.current - speed;
        } else {
            next = layoutInfo.current + speed;
        }
        int previous = layoutInfo.current;
        layoutInfo.checkAndUpdate(next);
        if(webView.getScrollY() > 0) {
            webView.scrollBy(0, layoutInfo.current - previous);
        }

        postUpdateIfNeeded(layoutInfo);
    }
}
