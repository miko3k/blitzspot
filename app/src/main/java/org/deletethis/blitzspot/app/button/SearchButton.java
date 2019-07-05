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
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.skyfishjy.library.RippleBackground;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.blitzspot.lib.icon.IconView;
import org.deletethis.search.parser.IconAddress;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SearchButton extends FrameLayout
{
    private static final SearchButtonCallback EMPTY_CALLBACK = new SearchButtonCallback() {
        @Override public void onButtonLongTouch() { }
        @Override public void onButtonClick() { }
        @Override public void preventButtonTimeout() { }
        @Override public void enableButtonTimeout() { }
        @Override public void onButtonDragged(int x, int y) { }
        @Override public Point getCurrentPosition() { return new Point(0,0); }
    };

    private RippleBackground rippleBackground;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private final int longPressTimeout;
    private final int clickDistance;
    private final Handler handler;
    private SearchButtonCallback listener = EMPTY_CALLBACK;
    private boolean dragging;
    private boolean longPressPossible;
    private boolean abortClick;
    private IconView iconView;
    private View button;
    private final Runnable emitLongPress = new Runnable() {
        @Override
        public void run() {
            Logging.BUTTON.i("long press detected");
            listener.onButtonLongTouch();
            abortClick = true;
        }
    };

    public SearchButton(@NonNull Context context) {
        this(context, null);
    }

    public SearchButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SearchButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.longPressTimeout = ViewConfiguration.getLongPressTimeout();
        this.clickDistance = (int) context.getResources().getDimension(R.dimen.clickDistance);
        this.handler = new Handler();
    }

    public void setCallback(SearchButtonCallback callback) {
        this.listener = (callback == null) ? EMPTY_CALLBACK : callback;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        rippleBackground = findViewById(R.id.ripple_background);
        button = findViewById(R.id.collapsed_iv);
        iconView = findViewById(R.id.icon);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Logging.BUTTON.d("onTouchEvent (dragging: " + dragging + "): " + event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                abortClick = false;
                abortLongPress();
                Logging.BUTTON.d("long press START");
                longPressPossible = true;
                handler.postDelayed(emitLongPress, longPressTimeout);

                //remember the initial position.
                Point currentPosition = listener.getCurrentPosition();
                initialX = currentPosition.x;
                initialY = currentPosition.y;
                dragging = false;

                //get the touch location
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                listener.preventButtonTimeout();
                break;

            case MotionEvent.ACTION_UP:
                abortLongPress();
                if (isShort(event) && !abortClick) {
                    listener.onButtonClick();
                }
                listener.enableButtonTimeout();
                break;

            case MotionEvent.ACTION_MOVE:
                if(!dragging && isShort(event)) {
                    // nothing
                } else {
                    dragging = true;
                    abortLongPress();

                    //Calculate the X and Y coordinates of the view.
                    int newX = initialX + (int) (event.getRawX() - initialTouchX);
                    int newY = initialY + (int) (event.getRawY() - initialTouchY);

                    listener.onButtonDragged(newX, newY);
                }
                break;

            default:
                listener.enableButtonTimeout();
                abortLongPress();
                break;
        }
        button.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return true;
    }

    public void setIcon(IconAddress icon) {
        iconView.setAddress(icon);
    }

    private boolean isShort(MotionEvent event) {
        int a = Math.abs((int) (event.getRawX() - initialTouchX));
        int b = Math.abs((int) (event.getRawY() - initialTouchY));
        return (a < clickDistance && b < clickDistance);
    }

    private void abortLongPress() {
        if(longPressPossible) {
            Logging.BUTTON.d("long press aborted");
            handler.removeCallbacks(emitLongPress);
            longPressPossible = false;
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(isInEditMode()) {
            return;
        }
        Logging.BUTTON.i("attached");
        rippleBackground.startRippleAnimation();
        listener.enableButtonTimeout();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(isInEditMode()) {
            return;
        }
        Logging.BUTTON.i("detached");
        rippleBackground.stopRippleAnimation();
        abortLongPress();
    }
}
