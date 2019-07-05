package org.deletethis.blitzspot.lib.db.operations;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ResultSetHandler<T> {
    static <T> ResultSetHandler<Optional<T>> optional(RowHandler<T> item) {
        return cursor -> {
            if(!cursor.moveToNext()) {
                return Optional.empty();
            }
            T result = item.handle(cursor);

            if(cursor.moveToNext()) {
                throw new IllegalStateException("more then one row returned");
            }
            return Optional.of(result);
        };
    }

    static <T> ResultSetHandler<T> one(RowHandler<T> item) {
        return cursor -> {
            if(!cursor.moveToNext()) {
                throw new IllegalStateException("no rows returned");
            }
            T result = item.handle(cursor);

            if(cursor.moveToNext()) {
                throw new IllegalStateException("more then one row returned");
            }
            return result;
        };
    }

    static <T> ResultSetHandler<List<T>> list(RowHandler<T> item) {
        return cursor -> {
            ArrayList<T> result = new ArrayList<>();
            while(cursor.moveToNext()) {
                T obj = item.handle(cursor);
                if(obj != null) {
                    result.add(obj);
                }
            }
            return result;
        };
    }

    T handle(Cursor cursor);
}
