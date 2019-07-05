package org.deletethis.blitzspot.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.blitzspot.app.InstantState;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        Logging.SERVICE.d("BootDeviceReceiver onReceive, action is " + action);

        if(action != null) {
            switch (action) {
                case "android.intent.action.QUICKBOOT_POWERON":
                case Intent.ACTION_BOOT_COMPLETED:
                    InstantState.get(context).start(context);
            }
        }
    }
}