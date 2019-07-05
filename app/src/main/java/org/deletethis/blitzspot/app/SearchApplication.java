package org.deletethis.blitzspot.app;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import org.deletethis.blitzspot.lib.Logging;

import io.fabric.sdk.android.Fabric;

public class SearchApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());


        //Context applicationContext = getApplicationContext();

        //BuiltinPlugins.get(applicationContext);
        Logging.MAIN.d("Application started");
    }
}
