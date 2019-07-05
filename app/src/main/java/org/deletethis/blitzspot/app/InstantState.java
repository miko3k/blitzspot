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
    private final static ImmutablePoint DEFAULT_LOCATION = new ImmutablePoint(10, 100);

    private static InstantState instance;

    private final File defaultFile;
    private final File locationFile;
    private final MutableLiveData<Boolean> running;
    private final MutableLiveData<byte[]> defaultPlugin;
    private final MutableLiveData<ImmutablePoint> location;
    private final Handler handler;
    private final AtomicReference<ImmutablePoint> locationToWrite = new AtomicReference<>();

    private static final String DEFAULT_FILENAME = "default_plugin";
    private static final String LOCATION_FILENAME = "location";

    public static InstantState get(Context context) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("Must be invoked from the main thread.");
        }

        if(instance == null) {
            instance = new InstantState(context);
        }

        return instance;
    }

    private void loadStuff() {

        AsyncTask.execute(() -> {
            byte[] defaultPlugin = null;
            ImmutablePoint location = DEFAULT_LOCATION;

            try {
                byte[] body = Files.asByteSource(defaultFile).read();
                if(body.length != 0) {
                    defaultPlugin = body;
                }
            } catch(Exception e) {
                Logging.SERVICE.e("unable to read " + defaultFile, e);
            }

            try {
                byte[] body = Files.asByteSource(locationFile).read();
                location = ImmutablePoint.deserialize(body);
            } catch(Exception e) {
                Logging.SERVICE.e("unable to read " + locationFile, e);
            }

            Logging.SERVICE.i(
                    "restored values: default plugin: " +
                            (defaultPlugin == null ? "null" : defaultPlugin.length + " bytes") +
                            ", location: " + location);

            byte[] finalDefaultPlugin = defaultPlugin;
            ImmutablePoint finalLocation = location;
            this.defaultPlugin.postValue(finalDefaultPlugin);
            this.location.postValue(finalLocation);
        });
    }

    private final Runnable WRITE_LOCATION = new Runnable() {
        @Override
        public void run() {
            final ImmutablePoint location = locationToWrite.getAndSet(null);
            if(location == null) {
                return;
            }

            AsyncTask.execute(() -> {
                try {
                    Files.asByteSink(locationFile).write(location.serialize());
                    Logging.SERVICE.i("location: saved value: " + location);
                } catch (IOException e) {
                    Logging.SERVICE.e("unable to write " + defaultFile, e);
                }
            });
        }
    };


    private InstantState(Context context) {
        this.running = new MutableLiveData<>();
        this.defaultPlugin = new MutableLiveData<>();
        this.location = new MutableLiveData<>();
        // running is for sure false
        this.running.setValue(false);
        this.location.setValue(DEFAULT_LOCATION);
        this.defaultFile = new File(context.getFilesDir(), DEFAULT_FILENAME);
        this.locationFile = new File(context.getFilesDir(), LOCATION_FILENAME);
        this.handler = new Handler();

        defaultPlugin.observeForever(def -> AsyncTask.execute(() -> {
            byte [] content = (def == null) ? new byte[0] : def;
            try {
                Files.asByteSink(defaultFile).write(content);
                Logging.SERVICE.iOnly("showNotification: saved value: " + new String(content, StandardCharsets.ISO_8859_1));
            } catch (IOException e) {
                Logging.SERVICE.e("unable to write " + defaultFile, e);
            }
        }));

        location.observeForever(def -> AsyncTask.execute(() -> {
            locationToWrite.set(def);
            handler.removeCallbacks(WRITE_LOCATION);
            handler.postDelayed(WRITE_LOCATION, 1500);
        }));


        loadStuff();
    }

    public LiveData<Boolean> getRunning() {
        return running;
    }

    public LiveData<byte[]> getDefaultPlugin() {
        return defaultPlugin;
    }

    public MutableLiveData<ImmutablePoint> getLocation() {
        return location;
    }

    public void notifyStarted() {
        running.setValue(true);
    }

    public void notifyStopped() {
        running.setValue(false);
    }

    public static boolean isEnabled(Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        int componentEnabledSetting = pm.getComponentEnabledSetting(receiver);
        return componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }

    public void start(Context context) {
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

    public void stop(Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        context.stopService(new Intent(context, ButtonService.class));
    }

    public void setDefaultPlugin(SearchPlugin searchPlugin) {
        Logging.SERVICE.i("set default plugin: " + searchPlugin);
        this.defaultPlugin.setValue(searchPlugin == null ? null : searchPlugin.serialize());
    }

    public void setLocation(int x, int y) {
        this.location.setValue(new ImmutablePoint(x, y));
    }
}
