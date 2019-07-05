package org.deletethis.blitzspot.lib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.deletethis.blitzspot.lib.Logging;

/**
 * Not sure if this class is needed, because SQLLiteDatabase does some reference counting.
 *
 * Anyways, if not, we have this one.
 */
public class DatabaseProviderImpl implements DatabaseProvider {
    public interface HelperSupplier {
        SQLiteOpenHelper get(Context context);
    }

    private final HelperSupplier helper;
    private SQLiteDatabase database;
    private int refcount;


    public DatabaseProviderImpl(HelperSupplier helper) {
        this.helper = helper;
    }

    private synchronized SQLiteDatabase getDatabase(Context context) {
        if(database == null) {
            Logging.DB.i("actually opening the database");
            database = helper.get(context).getWritableDatabase();
        }
        return database;
    }

    private void closeDatabase() {
        refcount--;
        Logging.DB.d("database closed, current refcount = " + refcount);
        if(refcount == 0) {
            Logging.DB.i("actually closing the database");
            database.close();
            database = null;
        }
    }


    @Override
    public synchronized Database open(Context context) {
        Logging.DB.d("open database, current refcount = " + refcount);
        ++refcount;
        return new Database() {
            @Override
            public SQLiteDatabase getWritableDatabase() {
                return getDatabase(context);
            }

            @Override
            public void close() {
                closeDatabase();
            }
        };
    }
}
