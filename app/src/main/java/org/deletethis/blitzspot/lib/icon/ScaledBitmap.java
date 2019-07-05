package org.deletethis.blitzspot.lib.icon;

import android.graphics.Bitmap;

class ScaledBitmap {
    private final int originalW, originalH;
    private final Bitmap scaledBitmap;

    public ScaledBitmap(Bitmap scaledBitmap, int originalW, int originalH) {
        this.originalW = originalW;
        this.originalH = originalH;
        this.scaledBitmap = scaledBitmap;
    }

    int getOriginalWidth() {
        return originalW;
    }

    int getOriginalHeight() {
        return originalH;
    }

    Bitmap getScaledBitmap() {
        return scaledBitmap;
    }
}