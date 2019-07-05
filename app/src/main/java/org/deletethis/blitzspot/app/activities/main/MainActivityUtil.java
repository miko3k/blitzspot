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

package org.deletethis.blitzspot.app.activities.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

class MainActivityUtil {
    static void animatePopupAppearance(Context context, ViewGroup popup, boolean above) {
        if (above) {
            popup.setPivotY(popup.getHeight());
        } else {
            popup.setPivotY(0);
        }
        popup.setPivotX(0);
        popup.setAlpha(0f);
        popup.setScaleX(0f);
        popup.setScaleY(0f);

        popup.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(context.getResources().getInteger(android.R.integer.config_shortAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        popup.setAlpha(1);
                        popup.setScaleX(1);
                        popup.setScaleY(1);
                    }
                });

        popup.setVisibility(View.VISIBLE);
    }

    static void animatePopupDisappearance(Context context, ViewGroup popup) {
        popup.animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(context.getResources().getInteger(android.R.integer.config_mediumAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        popup.setVisibility(View.INVISIBLE);
                        popup.setAlpha(1);
                        popup.setScaleX(1);
                        popup.setScaleY(1);
                    }
                });
    }

    static CoordinatorLayout.LayoutParams createCoordinatorLayoutParams(int x, int y) {
        CoordinatorLayout.LayoutParams layout = new CoordinatorLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout.setMarginStart(x);
        layout.topMargin = y;
        return layout;
    }

    static byte [] readUri(Context context, Uri uri) {
        if(uri == null)
            return null;

        try(InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if(inputStream == null) {
                return null;
            } else {
                return ByteStreams.toByteArray(inputStream);
            }
        } catch (IOException e) {
            return null;
        }
    }
}
