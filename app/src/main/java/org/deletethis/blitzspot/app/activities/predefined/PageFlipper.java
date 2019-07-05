package org.deletethis.blitzspot.app.activities.predefined;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.viewpager.widget.ViewPager;

/**
 *
 * <a href='https://stackoverflow.com/a/30976741'>From here</a>
 */
class PageFlipper {
    private final ViewPager pager;
    private final int duration;

    PageFlipper(ViewPager pager, int duration) {

        this.pager = pager;
        this.duration = duration;
    }

    void flip(final boolean forward) {

        ValueAnimator animator = ValueAnimator.ofInt(0, pager.getWidth() - ( forward ? pager.getPaddingLeft() : pager.getPaddingRight() ));
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pager.endFakeDrag();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                pager.endFakeDrag();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private int oldDragPosition = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int dragPosition = (Integer) animation.getAnimatedValue();
                int dragOffset = dragPosition - oldDragPosition;
                oldDragPosition = dragPosition;
                pager.fakeDragBy(dragOffset * (forward ? -1 : 1));
            }
        });

        animator.setDuration(duration);
        pager.beginFakeDrag();
        animator.start();
    }

}
