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

package org.deletethis.blitzspot.app.builtin.list;

import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;

public class GroupItem {
    private final @StringRes int title;
    private final @DrawableRes int titleIcon;
    private final String identifier;
    private final @RawRes int source;

    GroupItem(@StringRes int title, @DrawableRes int titleIcon, String identifier, int source) {
        this.title = title;
        this.titleIcon = titleIcon;
        this.identifier = identifier;
        this.source = source;
    }

    public @StringRes int getTitle() {
        return title;
    }

    public @DrawableRes int getTitleIcon() {
        return titleIcon;
    }

    public String getIdentifier() {
        return identifier;
    }

    public @RawRes int getSource() {
        return source;
    }
}