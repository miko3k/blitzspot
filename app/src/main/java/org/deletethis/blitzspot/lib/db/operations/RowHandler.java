package org.deletethis.blitzspot.lib.db.operations;

import android.database.Cursor;

public interface RowHandler<T> {
    T handle(Cursor cursor);

    RowHandler<Double> DOUBLE = cursor -> cursor.getDouble(0);
    RowHandler<Long> LONG = cursor -> cursor.getLong(0);
}
