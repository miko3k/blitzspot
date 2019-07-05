package org.deletethis.blitzspot.lib.db;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class ExecutorSingleton {
    private static Executor thread;

    public static Executor get() {
        if(thread == null) {
            thread = Executors.newSingleThreadExecutor();
        }
        return thread;
    }
}
