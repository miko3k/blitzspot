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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;

import org.deletethis.blitzspot.app.activities.LaunchSearchActivity;
import org.deletethis.blitzspot.app.builtin.PluginFactory;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.blitzspot.app.InstantState;
import org.deletethis.blitzspot.lib.ImmutablePoint;
import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.activities.jump.JumpActivity;
import org.deletethis.blitzspot.lib.icon.IconView;
import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPlugin;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import androidx.lifecycle.MutableLiveData;

public class SearchButtonManager implements SearchButtonCallback {

    private final WindowManager mWindowManager;
    private final SearchButton searchButton;
    private final Context context;
    private final InstantState applicationState;
    private final LayoutManager layoutManager;
    private final MutableLiveData<ImmutablePoint> locationLiveData;
    private boolean isVisible;
    private boolean shouldBeVisible;
    private Handler handler;
    private String currentQuery = "";
    private final Runnable HIDE = this::removeSearchButtonFromParent;

    private void dequeueHide() {
        handler.removeCallbacks(HIDE);
    }

    private void enqueueHide() {
        dequeueHide();
        handler.postDelayed(HIDE, 3000);
    }

    public SearchButtonManager(Context context) {
        this.context = context;
        this.applicationState = InstantState.get(context);
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if(this.mWindowManager == null) {
            throw new UnsupportedOperationException();
        }
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        this.searchButton = (SearchButton)layoutInflater.inflate(R.layout.floating_button, null);
        this.searchButton.setCallback(this);

        this.isVisible = false;
        this.layoutManager = new LayoutManager(context);
        this.handler = new Handler();
        this.locationLiveData = applicationState.getLocation();
    }

    private ImmutablePoint getLocation() {
        ImmutablePoint loc = locationLiveData.getValue();
        if(loc == null)
            throw new IllegalStateException();

        return loc;
    }

    private void showAndApplyLayout() {
        ImmutablePoint loc = getLocation();

        LayoutInfo layout = layoutManager.getLayoutInfo(loc.getX(), loc.getY());
        if(!isVisible) {
            searchButton.setVisibility(View.VISIBLE);
            mWindowManager.addView(searchButton, layout.getLayoutParams());
            isVisible = true;
        } else {
            mWindowManager.updateViewLayout(searchButton, layout.getLayoutParams());
        }
        searchButton.setTranslationX(layout.getTranslationX());
        searchButton.setTranslationY(layout.getTranslationY());
    }

    public void newClipboardText(String text) {
        ImmutablePoint loc = getLocation();
        int x = loc.getX();
        int y = loc.getY();

        shouldBeVisible = true;
        text = text.trim();
        Logging.BUTTON.i("new text: " + text);
        Rect rect = layoutManager.getAvailableRect(layoutManager.getDisplaySize());
        if(x < rect.left) x = rect.left;
        if(x > rect.right) x = rect.right;
        if(y < rect.top) y = rect.top;
        if(y > rect.bottom) y = rect.bottom;

        ImmutablePoint newLocation = new ImmutablePoint(x, y);
        if(!newLocation.equals(loc))
            locationLiveData.setValue(newLocation);

        enqueueHide();

        currentQuery = text.trim();

        showAndApplyLayout();
        searchButton.setScaleX(0);
        searchButton.setScaleY(0);
        searchButton.setAlpha(0);
        searchButton.clearAnimation();

        byte[] pluginBytes = InstantState.get(context).getDefaultPlugin().getValue();
        if(pluginBytes == null) {
            Logging.BUTTON.i("current plugin: none");
            searchButton.setIcon(IconView.getResourceAddress(R.drawable.jump_flash));
        } else{
            try {
                PluginFactory factory = PluginFactory.get(context);
                SearchPlugin plugin = factory.load(pluginBytes);
                Logging.BUTTON.iOnly("current plugin: " + new String(pluginBytes, StandardCharsets.ISO_8859_1));
                searchButton.setIcon(plugin.getIcon());
            } catch (PluginParseException e) {
                throw new IllegalStateException(e);
            }

        }

        searchButton.animate()
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setDuration(context.getResources().getInteger(android.R.integer.config_mediumAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        searchButton.setScaleX(1);
                        searchButton.setScaleY(1);
                        searchButton.setAlpha(1);
                        Logging.BUTTON.i("appear animation finished");
                    }
                });
    }

    @Override
    public void onButtonLongTouch() {
        dequeueHide();
        removeSearchButtonFromParentLongClick();
        Logging.BUTTON.i("long click");
        searchUsingPlugin(null);
    }

    @Override
    public void onButtonClick() {
        Logging.BUTTON.i("click");
        dequeueHide();
        removeSearchButtonFromParentClick();
        byte[] plugin = InstantState.get(context).getDefaultPlugin().getValue();
        searchUsingPlugin(plugin);
    }


    private void searchUsingPlugin(byte[] plugin) {
        if(plugin == null) {
            Intent intent = new Intent(context, JumpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(JumpActivity.QUERY, currentQuery);
            context.startActivity(intent);
        } else {
            LaunchSearchActivity.launch(context, currentQuery, plugin, false, true);
        }
    }

    private void removeSearchButtonFromParent() {
        removeSearchButtonFromParent(anim -> anim.scaleX(0).scaleY(0).alpha(0));
    }

    private void removeSearchButtonFromParent(Consumer<ViewPropertyAnimator> howToAnimate) {
        shouldBeVisible = false;
        dequeueHide();
        if(isVisible) {
            Logging.BUTTON.i("hide animation started");
            ViewPropertyAnimator animator = searchButton.animate();
            howToAnimate.accept(animator);
            animator.setDuration(context.getResources().getInteger(android.R.integer.config_mediumAnimTime));
            animator.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    Logging.BUTTON.i("hide animation finished, should be visible = " + shouldBeVisible);
                    if (!shouldBeVisible && isVisible) {
                        // remote itself does not seem to be enough, the view sometimes blinked
                        searchButton.setVisibility(View.GONE);
                        mWindowManager.removeView(searchButton);
                        isVisible = false;
                    }
                }
            });

        }
    }

    private void removeSearchButtonFromParentLongClick() {
        removeSearchButtonFromParent(anim -> anim.scaleX(2f).scaleY(2f).alpha(0));
    }

    private void removeSearchButtonFromParentClick() {
        removeSearchButtonFromParent(anim -> anim.alpha(0));
    }



    @Override
    public void preventButtonTimeout() {
        dequeueHide();
    }

    @Override
    public void enableButtonTimeout() {
        enqueueHide();
    }

    @Override
    public void onButtonDragged(int x, int y) {
        locationLiveData.setValue(new ImmutablePoint(x, y));
        showAndApplyLayout();
    }

    @Override
    public Point getCurrentPosition() {
        ImmutablePoint location = getLocation();
        return new Point(location.getX(), location.getY());
    }


    public void destroy() {
        dequeueHide();
        removeSearchButtonFromParent();
    }
}
