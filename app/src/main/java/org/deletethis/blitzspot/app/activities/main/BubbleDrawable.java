package org.deletethis.blitzspot.app.activities.main;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import org.deletethis.blitzspot.app.R;

import androidx.annotation.NonNull;

/**
 * Not used but I do not have heart to delete this lovely thing.
 *
 * Integer division warning is disabled as well, we don't want fuzzy sub-pixel lines.
 */
@SuppressWarnings({"unused", "IntegerDivisionInFloatingPointContext"})
class BubbleDrawable extends Drawable {
    private final Paint paint;
    private final int outerSizeH;
    private final int innerSizeH;
    private final int outerSizeV;
    private final int innerSizeV;

    private final int arrowLeft;
    private final int arrowRight;
    private final int arrowCenter;
    private final int arrowSize;

    private final boolean arrowOnTop;

    public BubbleDrawable(Resources resources, boolean arrowOnTop) {
        this.arrowOnTop = arrowOnTop;

        int shadowRadius = (int)resources.getDimension(R.dimen.bubbleShadowRadius);
        int cornerRadius = (int)resources.getDimension(R.dimen.bubbleCornerRadius);
        int arrowSize = (int)resources.getDimension(R.dimen.bubbleArrowSize);

        this.paint = new Paint();
        this.paint.setColor(resources.getColor(R.color.colorMainBubble, null));
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setShadowLayer(
                shadowRadius,
                0, 0,
                resources.getColor(R.color.colorMainBubbleShadow, null));

        this.outerSizeH = shadowRadius;
        this.innerSizeH = shadowRadius + cornerRadius;
        this.outerSizeV = shadowRadius + arrowSize;
        this.innerSizeV = shadowRadius + cornerRadius + arrowSize;
        this.arrowSize = arrowSize;
        this.arrowLeft = (int)resources.getDimension(R.dimen.bubbleArrowLeft);
        this.arrowRight = (int)resources.getDimension(R.dimen.bubbleArrowRight);
        this.arrowCenter = (arrowLeft+arrowRight)/2;
    }

    private void lineWithArrowTop(Path p, int x, int y) {
        p.lineTo(arrowLeft, y);
        p.lineTo(arrowCenter, y-arrowSize);
        p.lineTo(arrowRight, y);
        p.lineTo(x, y);
    }

    private void lineWithArrowBottom(Path p, int x, int y) {
        p.lineTo(arrowRight, y);
        p.lineTo(((arrowLeft+arrowRight)/2), y+arrowSize);
        p.lineTo(arrowLeft, y);
        p.lineTo(x, y);
    }

    public int getArrowCenter() {
        return arrowCenter;
    }

    private Path createPath(int w, int h) {
        int x1outer = outerSizeH;
        int x1inner = innerSizeH;
        int x2inner = w - innerSizeH;
        int x2outer = w - outerSizeH;

        int y1outer = outerSizeV;
        int y1inner = innerSizeV;
        int y2inner = h - innerSizeV;
        int y2outer = h - outerSizeV;

        Path path = new Path();
        path.moveTo(x1outer, y1inner);
        path.quadTo(x1outer, y1outer, x1inner, y1outer);

        if(arrowOnTop) {
            lineWithArrowTop(path, x2inner, y1outer);
        } else {
            path.lineTo(x2inner, y1outer);
        }

        path.quadTo(x2outer, y1outer, x2outer, y1inner);
        path.lineTo(x2outer, y2inner);
        path.quadTo(x2outer, y2outer, x2inner, y2outer);

        if(!arrowOnTop) {
            lineWithArrowBottom(path, x1inner, y2outer);
        } else {
            path.lineTo(x1inner, y2outer);
        }

        path.quadTo(x1outer, y2outer, x1outer, y2inner);
        path.close();
        return path;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        // Get the drawable's bounds
        int width = getBounds().width();
        int height = getBounds().height();
        Path path = createPath(width, height);

        // Draw a red circle in the center
        canvas.drawPath(path, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        // This method is required
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // This method is required
    }

    @Override
    public int getOpacity() {
        // Must be PixelFormat.UNKNOWN, TRANSLUCENT, TRANSPARENT, or OPAQUE
        return PixelFormat.TRANSLUCENT;
    }
}