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

package org.deletethis.blitzspot.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.google.common.io.Files;

import org.deletethis.blitzspot.app.service.BootReceiver;
import org.deletethis.blitzspot.app.service.ButtonService;
import org.deletethis.blitzspot.lib.ImmutablePoint;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.search.parser.SearchPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

@MainThread
public class InstantState {
    private static InstantState instance;

    private final MutableLiveData<Boolean> running;
    private final Handler handler;
    private final Context context;

    public static InstantState get(Context context) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("Must be invoked from the main thread.");
        }

        if(instance == null) {
            instance = new InstantState(context);
        }

        return instance;
    }


    private InstantState(Context context) {
        this.running = new MutableLiveData<>();
        // running is for sure false
        this.running.setValue(false);
        this.handler = new Handler();
        this.context = context.getApplicationContext();
    }

    public LiveData<Boolean> getRunning() {
        return running;
    }

    public void notifyStarted() {
        running.setValue(true);
    }

    public void notifyStopped() {
        running.setValue(false);
    }

    public boolean isEnabled() {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        int componentEnabledSetting = pm.getComponentEnabledSetting(receiver);
        return componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }

    public void start() {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, ButtonService.class));
        } else {
            context.startService(new Intent(context, ButtonService.class));
        }
    }

    public void refresh() {
        if(isEnabled()) {
            start();
        }
    }

    public void stop() {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        context.stopService(new Intent(context, ButtonService.class));
    }
}
