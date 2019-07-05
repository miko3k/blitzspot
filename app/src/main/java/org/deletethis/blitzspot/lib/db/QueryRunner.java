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
import android.os.CancellationSignal;
import android.os.Handler;

import org.deletethis.blitzspot.lib.Logging;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class QueryRunner implements DefaultLifecycleObserver {
    private final Handler handler;
    private final Database database;
    private final Set<BaseRequest> runningQueries = ConcurrentHashMap.newKeySet();
    private final CancellationSignal cancellationSignal;
    private final Executor executor;
    private boolean terminated = false;

    private void removeFinished() {
        runningQueries.removeIf(BaseRequest::isFinished);
    }

    public QueryRunner(DatabaseProvider databaseProvider, Context context, LifecycleOwner lifecycleOwner, Executor executor) {
        this.database = databaseProvider.open(context);
        this.cancellationSignal = new CancellationSignal();
        this.handler = new Handler();
        this.executor = Objects.requireNonNull(executor);
        if(lifecycleOwner != null) {
            lifecycleOwner.getLifecycle().addObserver(this);
        }

    }

    public QueryRunner(DatabaseProvider openHelperProvider, Context context, LifecycleOwner lifecycleOwner) {
        this(openHelperProvider, context, lifecycleOwner, ExecutorSingleton.get());
    }

    private class ResultDelivery<T> implements Consumer<T> {
        private final Consumer<T> consumer;

        ResultDelivery(Consumer<T> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t) {
            removeFinished();
            handler.post(() -> consumer.accept(t));
        }
    }

    private <T> void doRun(
            DatabaseOperation<T> operation,
            Consumer<T> resultConsumer,
            CancellationSignal cancellationSignal) {

        if(terminated) {
            throw new IllegalStateException("no more queries, please!");
        }

        Objects.requireNonNull(operation);

        if(resultConsumer != null) {
            resultConsumer = new ResultDelivery<>(resultConsumer);
        }

        Request<T> req = new Request<>(database, operation, resultConsumer, cancellationSignal);
        runningQueries.add(req);
        executor.execute(req);
    }

    public <T> void run(DatabaseOperation<T> operation, Consumer<T> resultConsumer) {
        doRun(operation, resultConsumer, cancellationSignal);
    }

    @SuppressWarnings("unused")
    public <T> void run(DatabaseOperation<T> operation) {
        doRun(operation, null, cancellationSignal);
    }

    public <T> void runUncancellable(DatabaseOperation<T> operation, Consumer<T> resultConsumer) {
        doRun(operation, resultConsumer, null);
    }

    @SuppressWarnings("unused")
    public <T> void runUncancellable(DatabaseOperation<T> operation) {
        doRun(operation, null, null);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        terminated = true;

        BaseRequest closeRequest = new BaseRequest() {
            boolean finished = false;

            @Override
            synchronized public boolean isFinished() {
                return finished;
            }

            @Override
            synchronized public void run() {
                database.close();
                finished = true;
            }
        };

        runningQueries.add(closeRequest);
        executor.execute(closeRequest);

        cancellationSignal.cancel();
        removeFinished();
        int n = 0;
        while(!runningQueries.isEmpty()) {
            if(n > 100) {
                Logging.DB.i("still running queries (" + runningQueries.size() + "), waiting...");
                n = 0;
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                // fuck it
                Logging.DB.i("interrupted exception", e);
                break;
            }
            removeFinished();
            ++n;
        }
    }
}