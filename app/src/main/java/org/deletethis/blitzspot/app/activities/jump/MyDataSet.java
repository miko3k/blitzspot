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

package org.deletethis.blitzspot.app.activities.jump;

import org.deletethis.blitzspot.app.dao.PluginWithId;
import org.deletethis.blitzspot.app.fragments.choose.DataSet;
import org.deletethis.search.parser.IconAddress;

import java.util.List;
import java.util.function.Consumer;

class MyDataSet implements DataSet {
    private final List<PluginWithId> plugins;
    private final Consumer<byte[]> consumer;

    MyDataSet(List<PluginWithId> plugins, Consumer<byte[]> consumer) {
        this.plugins = plugins;
        this.consumer = consumer;
    }


    @Override
    public IconAddress getIcon(int position) {
        return plugins.get(position).getSearchPlugin().getIcon();
    }

    @Override
    public String getName(int position) {
        return plugins.get(position).getSearchPlugin().getName();
    }

    @Override
    public void selected(int position) {
        consumer.accept(plugins.get(position).getSearchPlugin().serialize());
    }

    @Override
    public int getCount() {
        return plugins.size();
    }
}
