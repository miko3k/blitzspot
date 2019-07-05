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

package org.deletethis.blitzspot.app.activities.defplugin;

import android.content.Context;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.dao.PluginWithId;
import org.deletethis.blitzspot.app.fragments.choose.DataSet;
import org.deletethis.blitzspot.lib.icon.IconView;
import org.deletethis.search.parser.IconAddress;

import java.util.List;
import java.util.function.Consumer;

class MyDataSet implements DataSet {
    private final List<PluginWithId> plugins;
    private final String alwaysAsk;
    private final Consumer<byte[]> click;
    private static final IconAddress ICON = IconView.getResourceAddress(R.drawable.plugin_icon_nothing);

    MyDataSet(Context context, List<PluginWithId> plugins, Consumer<byte[]> click) {
        this.plugins = plugins;
        this.alwaysAsk = context.getString(R.string.always_ask);
        this.click = click;
    }

    @Override
    public IconAddress getIcon(int position) {
        if (position == 0) return ICON;
        else return plugins.get(position - 1).getSearchPlugin().getIcon();
    }

    @Override
    public String getName(int position) {
        if (position == 0) return alwaysAsk;
        else return plugins.get(position - 1).getSearchPlugin().getName();
    }

    @Override
    public void selected(int position) {
        if (position == 0) {
            click.accept(null);
        } else {
            click.accept(plugins.get(position - 1).getSearchPlugin().serialize());
        }
    }

    @Override
    public int getCount() {
        return plugins.size() + 1;
    }
}
