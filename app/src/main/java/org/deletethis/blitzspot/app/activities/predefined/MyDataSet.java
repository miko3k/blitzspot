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

import android.content.Context;

import org.deletethis.blitzspot.app.builtin.BuiltinItem;
import org.deletethis.blitzspot.app.fragments.choose.DataSet;
import org.deletethis.search.parser.IconAddress;

import java.util.List;
import java.util.function.Consumer;

public class MyDataSet implements DataSet {
    private final List<BuiltinItem> data;
    private final Context context;
    private final Consumer<BuiltinItem> consumer;

    public MyDataSet(List<BuiltinItem> data, Context context, Consumer<BuiltinItem> consumer) {
        this.data = data;
        this.context = context;
        this.consumer = consumer;
    }

    @Override
    public IconAddress getIcon(int position) {
        return data.get(position).getIcon(context);
    }

    @Override
    public String getName(int position) {
        return data.get(position).getName(context);
    }

    @Override
    public void selected(int position) {
        consumer.accept(data.get(position));
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isFancyArrowPresent(int position) {
        return data.get(position).getChildren().map(list -> !list.isEmpty()).orElse(false);
    }
}
