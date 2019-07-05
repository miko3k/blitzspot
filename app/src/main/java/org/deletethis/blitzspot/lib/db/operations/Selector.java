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
