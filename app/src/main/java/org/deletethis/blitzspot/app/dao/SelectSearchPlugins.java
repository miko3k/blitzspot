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

package org.deletethis.blitzspot.app.dao;

import android.content.Context;

import org.deletethis.blitzspot.app.builtin.PluginFactory;
import org.deletethis.blitzspot.lib.db.DatabaseOperation;
import org.deletethis.blitzspot.lib.db.operations.Selector;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPlugin;

import java.util.ArrayList;
import java.util.List;

public class SelectSearchPlugins {
    private static final DatabaseOperation<List<RawPluginWithId>> RAW = Selector.query(
            DbConfig.ENGINES,
            new String[]{DbConfig.ENGINE_ID, DbConfig.ENGINE_DATA, DbConfig.ENGINE_ORDERING},
            null,
            null,
            DbConfig.ENGINE_ORDERING
    ).list(cursor -> {
        long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(DbConfig.ENGINE_ID));
        byte[] data = cursor.getBlob(cursor.getColumnIndexOrThrow(DbConfig.ENGINE_DATA));
        double ordering = cursor.getDouble(cursor.getColumnIndexOrThrow(DbConfig.ENGINE_ORDERING));
        return new RawPluginWithId(itemId, data, ordering);
    });

    private static List<PluginWithId> parse(Context context, List<RawPluginWithId> list) {
        List<PluginWithId> result = new ArrayList<>(list.size());
        PluginFactory pluginFactory = PluginFactory.get(context);

        for(RawPluginWithId raw: list) {
            try {
                long itemId = raw.getId();
                byte [] data = raw.getData();
                double ordering = raw.getOrdering();
                SearchPlugin searchEngine = pluginFactory.load(data);
                result.add(new PluginWithId(itemId, searchEngine, ordering));
            } catch (PluginParseException e) {
                Logging.MAIN.e("unable to parse search plugin", e);
                return null;
            }
        }
        return result;
    }

    public static DatabaseOperation<List<PluginWithId>> get(Context context) {
        return (database, cancel) -> parse(context, RAW.execute(database, cancel));
    }
}
