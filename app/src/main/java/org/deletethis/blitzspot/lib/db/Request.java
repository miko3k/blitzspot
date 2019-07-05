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

import android.database.sqlite.SQLiteDatabase;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;

import org.deletethis.blitzspot.lib.Logging;

import java.util.Objects;
import java.util.function.Consumer;

class Request<T> implements BaseRequest {
    private final Database database;
    private final DatabaseOperation<T> operation;
    private final Consumer<T> onResult;
    private final CancellationSignal cancellationSignal;
    private boolean finished;

    Request(Database database, DatabaseOperation<T> operation, Consumer<T> onResult, CancellationSignal cancellationSignal) {
        this.database = Objects.requireNonNull(database);
        this.operation = Objects.requireNonNull(operation);
        this.onResult = onResult;
        this.cancellationSignal = cancellationSignal;
        this.finished = false;
    }

    @Override
    synchronized public boolean isFinished() {
        return finished;
    }

    synchronized private void setFinished() {
        this.finished = true;
    }

    @Override
    public void run() {
        Request<T> req = this;

        try {
            try {
                SQLiteDatabase writableDatabase = database.getWritableDatabase();

                T result = operation.execute(writableDatabase, cancellationSignal);
                if (cancellationSignal == null || !cancellationSignal.isCanceled()) {
                    if (onResult != null) {
                        onResult.accept(result);
                    }
                }
            } catch (OperationCanceledException cancelled) {
                Logging.DB.i("database operation cancelled");
            }
        } finally {
            req.setFinished();
        }

    }
}
