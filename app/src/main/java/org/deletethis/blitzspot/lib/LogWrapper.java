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
