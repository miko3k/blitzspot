package org.deletethis.blitzspot.lib.db;

import android.database.sqlite.SQLiteDatabase;

public interface Database extends AutoCloseable {
    /** This method might be slow */
    SQLiteDatabase getWritableDatabase();
    void close();
}
