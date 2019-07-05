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
