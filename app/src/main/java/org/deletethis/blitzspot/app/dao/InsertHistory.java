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

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.CancellationSignal;

import org.deletethis.blitzspot.app.activities.search.suggest.ChoiceProvider;
import org.deletethis.blitzspot.lib.db.DatabaseOperation;
import org.deletethis.blitzspot.lib.db.operations.RowHandler;
import org.deletethis.blitzspot.lib.db.operations.Selector;
import org.deletethis.blitzspot.lib.DatabaseUtilities;
import org.deletethis.blitzspot.lib.Logging;

import java.util.Objects;

public class InsertHistory implements DatabaseOperation<Void> {
    private final String engineKey;
    private final String query;
    private final String description;
    private final String url;
    private final String normalizedQuery;
    private final long now;

    private InsertHistory(String engineKey, String query, String description, String url) {
        this.engineKey = Objects.requireNonNull(engineKey);
        this.query = Objects.requireNonNull(query);
        this.description = description;
        this.url = url;
        this.normalizedQuery = DatabaseUtilities.normalize(Objects.requireNonNull(query));
        this.now = DatabaseUtilities.timestamp();
    }

    public InsertHistory(String engineKey, String query) {
        this(engineKey, query, null, null);

    }

    public InsertHistory(String engineKey, ChoiceProvider.Choice choice) {
        this(engineKey, choice.getSearchQuery().getAnyValue(), choice.getDescription(), choice.getUri());
    }

    private long baseRecord(SQLiteDatabase database, CancellationSignal cancel) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbConfig.HISTORY_NORMALIZED_QUERY, normalizedQuery);
            contentValues.put(DbConfig.HISTORY_LAST_USED, now);
            return database.insertOrThrow(DbConfig.HISTORY, null, contentValues);
        } catch (SQLiteConstraintException exception) {
            long id = Selector.rawQuery("SELECT " + DbConfig.HISTORY_ID +
                            " FROM " + DbConfig.HISTORY +
                            " WHERE " + DbConfig.HISTORY_NORMALIZED_QUERY + " = ?",
                    normalizedQuery).one(RowHandler.LONG).execute(database, null);

            ContentValues contentValues = new ContentValues();
            contentValues.put(DbConfig.HISTORY_LAST_USED, now);
            database.update(DbConfig.HISTORY, contentValues, DbConfig.HISTORY_ID + " = " + id, null);
            return id;
        }
    }

    private boolean historyRecord(SQLiteDatabase database, long historyId, CancellationSignal cancel) {
        ContentValues values = new ContentValues();
        values.put(DbConfig.CHOICE_QUERY, query);
        values.put(DbConfig.CHOICE_DESCRIPTION, description);
        values.put(DbConfig.CHOICE_URL, url);
        values.put(DbConfig.CHOICE_LAST_USED, now);

        int rpc = database.update(DbConfig.CHOICES,
                values,
                DbConfig.CHOICE_HISTORY_ID + " = " + historyId + " AND " + DbConfig.CHOICE_ENGINE_KEY + " = ?",
                new String[]{engineKey});

        if (rpc >= 1) {
            return false;
        }

        values.put(DbConfig.CHOICE_HISTORY_ID, historyId);
        values.put(DbConfig.CHOICE_ENGINE_KEY, engineKey);

        database.insertOrThrow(DbConfig.CHOICES, null, values);
        return true;
    }

    @Override
    public Void execute(SQLiteDatabase db, CancellationSignal cancel) {
        long id = baseRecord(db, cancel);
        boolean inserted = historyRecord(db, id, cancel);

        Logging.SEARCH.i("HISTORY: id: " + id + ", engine key: " + engineKey + ", new row: " + inserted + ", query: " + normalizedQuery);

        return null;
    }
}
