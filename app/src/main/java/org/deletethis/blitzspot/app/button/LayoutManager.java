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
