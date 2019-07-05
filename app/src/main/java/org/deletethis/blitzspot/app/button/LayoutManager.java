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

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.WindowManager;

import org.deletethis.blitzspot.app.R;

class LayoutManager {
    private final WindowManager mWindowManager;
    private final int floatingWindowSize;
    private final int buttonOverlap;

    public LayoutManager(Context context) {
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if(this.mWindowManager == null) {
            throw new UnsupportedOperationException();
        }
        this.floatingWindowSize = (int) context.getResources().getDimension(R.dimen.buttonTotalSite);
        this.buttonOverlap = (int) context.getResources().getDimension(R.dimen.buttonOverlap);
    }

    public LayoutInfo getLayoutInfo(int x, int y) {
        return new LayoutInfo(getDisplaySize(), floatingWindowSize, x, y);
    }

    public Point getDisplaySize() {
        Point displaySize = new Point();
        mWindowManager.getDefaultDisplay().getSize(displaySize);
        return displaySize;
    }

    public Rect getAvailableRect(Point displaySize) {
        return new Rect(
                -buttonOverlap,
                -buttonOverlap,
                displaySize.x - floatingWindowSize + buttonOverlap,
                displaySize.y - floatingWindowSize + buttonOverlap
        );
    }
}
