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
import android.database.sqlite.SQLiteDatabase;
import android.os.CancellationSignal;

import org.deletethis.blitzspot.lib.db.DatabaseOperation;
import org.deletethis.blitzspot.lib.db.operations.RowHandler;
import org.deletethis.blitzspot.lib.db.operations.Selector;

import java.util.Collections;
import java.util.List;

public class InsertSearchPlugin implements DatabaseOperation<Void> {
    private final List<byte[]> data;

    public InsertSearchPlugin(byte[] data) {
        this.data = Collections.singletonList(data);
    }

    public InsertSearchPlugin(List<byte[]> data) {
        this.data = data;
    }


    @Override
    public Void execute(SQLiteDatabase db, CancellationSignal cancel) {
        double newOrdering = Selector.stringQuery(
                    "select max(" + DbConfig.ENGINE_ORDERING + ") from " + DbConfig.ENGINES)
                .optional(RowHandler.DOUBLE)
                .execute(db, cancel)
                .orElse(0.0);

        for(byte [] data: this.data) {
            newOrdering += 10000.0;
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbConfig.ENGINE_ORDERING, newOrdering);
            contentValues.put(DbConfig.ENGINE_DATA, data);
            db.insertOrThrow(DbConfig.ENGINES, null, contentValues);
        }
        return null;
    }
}
