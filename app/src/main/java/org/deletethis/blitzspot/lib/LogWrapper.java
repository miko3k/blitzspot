package org.deletethis.blitzspot.lib;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

public class LogWrapper {
    private final String tag;

    public LogWrapper(String tag) {
        this.tag = tag;
    }

    private boolean hasCrashlytics() {
        return true;
    }

    public void i(String message) {
        if(hasCrashlytics()) {
            Crashlytics.log(Log.INFO, tag, message);
        } else {
            iOnly(message);
        }
    }

    public void e(String message, Throwable e) {
        Log.e(tag, message, e);
        if(hasCrashlytics()) {
            Crashlytics.log(message);
            Crashlytics.logException(e);
        }
    }

    public void i(String message, Throwable e) {
        Log.i(tag, message, e);
        if(hasCrashlytics()) {
            Crashlytics.log(message);
            Crashlytics.logException(e);
        }
    }

    public void d(String message) {
        if(hasCrashlytics()) {
            Crashlytics.log(Log.DEBUG, tag, message);
        } else {
            dOnly(message);
        }
    }

    public void d(String message, Throwable e) {
        Log.d(tag, message, e);
        if(hasCrashlytics()) {
            Crashlytics.log(message);
            Crashlytics.logException(e);
        }
    }

    public void e(String message) {
        if(hasCrashlytics()) {
            Crashlytics.log(Log.ERROR, tag, message);
        } else {
            eOnly(message);
        }
    }

    public void iOnly(String message) {
        Log.i(tag, message);
    }

    public void dOnly(String message) {
        Log.d(tag, message);
    }

    public void eOnly(String message) {
        Log.e(tag, message);
    }
}
