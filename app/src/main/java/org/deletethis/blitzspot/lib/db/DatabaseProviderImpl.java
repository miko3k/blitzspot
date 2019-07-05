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
