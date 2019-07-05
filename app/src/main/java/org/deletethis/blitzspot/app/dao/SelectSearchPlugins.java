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
