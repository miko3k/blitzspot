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