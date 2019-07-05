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

package org.deletethis.blitzspot.lib.db.operations;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.CancellationSignal;

import org.deletethis.blitzspot.lib.db.DatabaseOperation;

import java.util.List;
import java.util.Optional;

@SuppressLint("Recycle")
public interface Selector {
    Cursor createCursor(SQLiteDatabase db, CancellationSignal cancel);

    default <T> DatabaseOperation<T> withHandler(ResultSetHandler<T> handler) {
        return (db, cancel) -> {
            try (Cursor cursor = createCursor(db, cancel)) {
                return handler.handle(cursor);
            }
        };
    }

    default <T> DatabaseOperation<Optional<T>> optional(RowHandler<T> handler) {
        return withHandler(ResultSetHandler.optional(handler));
    }

    default <T> DatabaseOperation<List<T>> list(RowHandler<T> handler) {
        return withHandler(ResultSetHandler.list(handler));
    }

    default <T> DatabaseOperation<T> one(RowHandler<T> handler) {
        return withHandler(ResultSetHandler.one(handler));
    }

    static Selector rawQuery(String sql, String... selectionArgs) {
        return (db, cancel) -> db.rawQuery(sql, selectionArgs, cancel);
    }

    static Selector stringQuery(String sql) {
        return (db, cancel) -> db.rawQuery(sql, null, cancel);
    }

    static Selector query(String table, String[] columns, String selection,
                          String[] selectionArgs, String orderBy) {

        String sql = SQLiteQueryBuilder.buildQueryString(
                false, table, columns, selection, null, null, orderBy, null);

        return (db, cancel) -> db.rawQuery(sql, selectionArgs, cancel);
    }

}
