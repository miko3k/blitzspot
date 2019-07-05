package org.deletethis.blitzspot.lib.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.CancellationSignal;

public interface DatabaseOperation<T> {
    T execute(SQLiteDatabase database, CancellationSignal cancel);
}
