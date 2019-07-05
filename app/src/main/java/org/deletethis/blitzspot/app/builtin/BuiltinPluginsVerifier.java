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

package org.deletethis.blitzspot.app.builtin;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import org.deletethis.blitzspot.app.BuildConfig;
import org.deletethis.blitzspot.lib.RawResource;
import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPluginFactory;

import androidx.annotation.DrawableRes;

class BuiltinPluginsVerifier {
    private final Resources resources;

    BuiltinPluginsVerifier(Resources resources) {
        this.resources = resources;
    }

    void verify(BuiltinPluginInfo info) {
        if (!BuildConfig.DEBUG)
            return;

        try {
            byte[] source = RawResource.cachedLoad(resources, info.getSource());
            // Let's not use the main PluginFactory for loading, because this
            // class is not initialized yet. Static method verify is safe.
            PluginFactory.verify(new SearchPluginFactory().loadSearchPlugin(source));
        } catch (PluginParseException e) {
            throw new IllegalArgumentException(e);
        }
        Drawable drawable = resources.getDrawable(info.getIcon(), null);
        if (drawable == null) {
            throw new IllegalArgumentException("NO DRAWABLE");
        }
    }

    private int iconW, iconH;

    void verifyIcon(@DrawableRes int icon) {
        if (!BuildConfig.DEBUG)
            return;

        Drawable drawable = resources.getDrawable(icon, null);
        if (drawable == null) {
            throw new IllegalArgumentException("NO DRAWABLE");
        }

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        if(w != h) {
            throw new IllegalStateException("non square icon: "  + icon);
        }

        if(iconW == 0) {
            iconW = w;
        }
        if(iconH == 0) {
            iconH = h;
        }
        if(w != iconW || h != iconH) {
            throw new IllegalStateException("weird size icon: "  + icon);
        }
    }
}
