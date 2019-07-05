package org.deletethis.blitzspot.app.activities.predefined;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

class FadePageTransformer implements ViewPager.PageTransformer {

    public void transformPage(View view, float position) {
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0f);
        } else if (position <= 0) { // [-1,0]
            view.setAlpha(1 + position);
        } else if (position <= 1) { // (0,1]
            view.setAlpha(1 - position);
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0f);
        }
    }
}