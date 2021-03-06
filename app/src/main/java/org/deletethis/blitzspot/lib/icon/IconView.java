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

package org.deletethis.blitzspot.lib.icon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.common.collect.Iterables;

import org.deletethis.blitzspot.app.R;
import org.deletethis.search.parser.IconAddress;
import org.deletethis.search.parser.UrlIconAddress;
import org.deletethis.search.parser.util.DataUrl;

public class IconView extends AppCompatImageView implements Observer<Bitmap> {

    private IconAddress address;
    private LiveData<Bitmap> bitmapLiveData;

    public static IconAddress getResourceAddress(@DrawableRes int drawable) {
        return new ResourceIconAddress(drawable);
    }

    public IconView(Context context) {
        this(context, null);
    }

    public IconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.CENTER);
    }

    private boolean resolveLocally() {
        if(address == null || Iterables.isEmpty(address)) {
            setImageResource(R.drawable.plugin_icon_default);
            return true;
        }
        if(address instanceof ResourceIconAddress) {
            int id = ((ResourceIconAddress) address).getResource();
            setImageResource(id);
            return true;
        }
        for(String url: address) {
            if(!DataUrl.looksLikeDataUrl(url))
                continue;

            DataUrl dataUrl = DataUrl.parse(url);
            byte[] data = dataUrl.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            setImageBitmap(bitmap);
            return true;
        }
        return false;
    }

    private void updateIcon() {
        if(isInEditMode()) {
            setBackgroundResource(android.R.color.holo_blue_light);
            setImageResource(R.drawable.plugin_icon_default);
            return;
        }

        PluginIconLoader instance = PluginIconLoader.getInstance(getContext());
        if(!resolveLocally()) {
            bitmapLiveData = instance.getIconBitmap((UrlIconAddress)address);
            bitmapLiveData.observeForever(this);
        } else {
            bitmapLiveData = null;
        }
    }

    public void setAddress(IconAddress address) {
        this.address = address;
        if(isAttachedToWindow()) {
            if(bitmapLiveData != null) {
                bitmapLiveData.removeObserver(this);
                bitmapLiveData = null;
            }
            updateIcon();
        }
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateIcon();
    }

    @Override protected void onDetachedFromWindow() {
        if(bitmapLiveData != null) {
            bitmapLiveData.removeObserver(this);
            bitmapLiveData = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void onChanged(Bitmap bitmap) {
        setImageBitmap(bitmap);
    }

}
