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
