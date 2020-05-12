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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;

import org.deletethis.blitzspot.app.InstantState;
import org.deletethis.blitzspot.app.Intents;
import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.activities.jump.JumpActivity;
import org.deletethis.blitzspot.app.activities.main.MainActivity;
import org.deletethis.blitzspot.app.dao.DbOpenHelper;
import org.deletethis.blitzspot.app.dao.PluginWithId;
import org.deletethis.blitzspot.app.dao.SelectSearchPlugins;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.blitzspot.lib.db.QueryRunner;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;

import java.util.Collections;
import java.util.List;


public class ButtonService extends LifecycleService {
    private static final String DEFAULT_CHANNEL = ButtonService.class.getName() + ".NOTIFICATION";
    private static final int ONGOING_NOTIFICATION_ID = 56;
    private static final int MAX_DIRECT = 2;

    private InstantState applicationState;
    private QueryRunner queryRunner;
    private List<PluginWithId> plugins = Collections.emptyList();

    public ButtonService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Logging.SERVICE.i("onCreate");

        queryRunner = new QueryRunner(DbOpenHelper.PROVIDER, this, this);
        applicationState = InstantState.get(this);

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(DEFAULT_CHANNEL,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(getString(R.string.notification_channel_description));
            if(notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        startForeground();
        applicationState.notifyStarted();

    }

    private void setPlugins(List<PluginWithId> plugins) {
        this.plugins = plugins;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ONGOING_NOTIFICATION_ID, createNotification());
    }

    private void refresh() {
        queryRunner.run((db, cancel) -> SelectSearchPlugins.get(this).execute(db, cancel), this::setPlugins);
    }


    private Notification createNotification() {
        Intent openAppIntent = new Intent(this, MainActivity.class);
        PendingIntent openApp =
                PendingIntent.getActivity(this, 0, openAppIntent, 0);

        Intent searchIntent = new Intent(this, JumpActivity.class);
        PendingIntent search =
                PendingIntent.getActivity(this, 0, searchIntent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, DEFAULT_CHANNEL);
        builder
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.status)
                .setContentTitle(getString(R.string.notification_text))
                .setShowWhen(false)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setContentIntent(search);

        for(PluginWithId plugin: this.plugins.subList(0, Math.min(plugins.size(), MAX_DIRECT))) {
            Intent directIntent = new Intent(this, JumpActivity.class);
            byte [] data = plugin.serialize();
            // pending intent stores extras only occasionally and generating request codes
            // is tricky, we would have to generate unique request codes for every update of this notification
            // maybe a private static variable would do?
            //
            // anyways, it's a crude data uri now
            String str = JumpActivity.DATA_URI_PREFIX + Base64.encodeToString(data, Base64.URL_SAFE);
            directIntent.setData(Uri.parse(str));
            PendingIntent direct =
                    PendingIntent.getActivity(this, 0, directIntent, 0);

            builder.addAction(R.drawable.status, plugin.getSearchPlugin().getName(), direct);
        }

        builder.addAction(R.drawable.status, getString(R.string.open_app), openApp);
        return builder.build();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Logging.SERVICE.i("onStartCommend: " + intent);
        int res = super.onStartCommand(intent,flags,startId);
        refresh();
        return res;
    }

    private void startForeground() {
        startForeground(ONGOING_NOTIFICATION_ID, createNotification());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        applicationState.notifyStopped();
        Logging.SERVICE.i("onDestroy");
    }

}