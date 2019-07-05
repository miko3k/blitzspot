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

package org.deletethis.blitzspot.app.button;

import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;

public class LayoutInfo {
    private final int translationX;
    private final int translationY;
    private final WindowManager.LayoutParams layoutParams;

    @SuppressWarnings("deprecation")
    private static int getWindowType() {
        if(Build.VERSION.SDK_INT >= 26) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            return WindowManager.LayoutParams.TYPE_PHONE;
        }
    }

    public LayoutInfo(Point displaySize, int floatingWindowSize, int x, int y) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                getWindowType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;

        int translationX, translationY;
        if(x < 0) {
            params.x = 0;
            translationX = x;
        } else if(x + floatingWindowSize >= displaySize.x) {
            params.x = displaySize.x - floatingWindowSize;
            translationX = x - params.x;
        } else {
            params.x = x;
            translationX = 0;
        }
        if(y < 0) {
            params.y = 0;
            translationY = y;
        } else if(y + floatingWindowSize >= displaySize.y) {
            params.y = displaySize.y - floatingWindowSize;
            translationY = y - params.y;
        } else {
            params.y = y;
            translationY = 0;
        }

        this.translationX = translationX;
        this.translationY = translationY;
        this.layoutParams = params;
    }

    public int getTranslationX() {
        return translationX;
    }

    public int getTranslationY() {
        return translationY;
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return layoutParams;
    }
}
