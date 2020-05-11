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

package org.deletethis.blitzspot.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import org.deletethis.blitzspot.app.InstantState;
import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.activities.jump.JumpActivity;
import org.deletethis.blitzspot.app.activities.main.MainActivity;
import org.deletethis.blitzspot.lib.Logging;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;


public class ButtonService extends LifecycleService {
    private static final String DEFAULT_CHANNEL = ButtonService.class.getName() + ".NOTIFICATION";
    private static final int ONGOING_NOTIFICATION_ID = 56;

    private InstantState applicationState;

    public ButtonService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logging.SERVICE.i("onCreate");

        applicationState = InstantState.get(this);

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(DEFAULT_CHANNEL,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(getString(R.string.notification_channel_description));
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            if(notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        startForeground();
        applicationState.notifyStarted();
    }

    private void startForeground() {
        Intent openAppIntent = new Intent(this, MainActivity.class);
        PendingIntent openApp =
                PendingIntent.getActivity(this, 0, openAppIntent, 0);

        Intent searchIntent = new Intent(this, JumpActivity.class);
        PendingIntent search =
                PendingIntent.getActivity(this, 0, searchIntent, 0);


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, DEFAULT_CHANNEL);

        builder
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSmallIcon(R.drawable.status)
                .setContentTitle(getString(R.string.blitzspot_instant))
                .setContentText(getString(R.string.notification_text))
                .setShowWhen(false)
                .setOngoing(true)
                .setContentIntent(search)
                .addAction(R.drawable.status, getString(R.string.open_app), openApp);

        Notification notification = builder.build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        applicationState.notifyStopped();

        Logging.SERVICE.i("onDestroy");
        //searchButtonManager.destroy();
    }

}