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
import org.deletethis.blitzspot.app.activities.settings.SettingsActivity;
import org.deletethis.blitzspot.app.button.SearchButtonManager;
import org.deletethis.blitzspot.lib.Logging;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;


public class ButtonService extends LifecycleService implements ClipboardManager.OnPrimaryClipChangedListener {
    private static final String DEFAULT_CHANNEL = ButtonService.class.getName() + ".NOTIFICATION";
    private static final int ONGOING_NOTIFICATION_ID = 56;

    private ClipboardManager clipboardManager;
    private InstantState applicationState;
    private SearchButtonManager searchButtonManager;

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

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if(clipboardManager == null)
            throw new IllegalStateException();

        clipboardManager.addPrimaryClipChangedListener(this);

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(DEFAULT_CHANNEL,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(getString(R.string.notification_channel_description));
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            if(notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        startForeground();
        applicationState.notifyStarted();

        searchButtonManager = new SearchButtonManager(this);
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, SettingsActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder bld = new NotificationCompat.Builder(this, DEFAULT_CHANNEL);
        bld.setPriority(NotificationCompat.PRIORITY_LOW);
        bld.setSmallIcon(R.drawable.status);
        bld.setContentTitle(getString(R.string.blitzspot_instant));
        bld.setContentText(getString(R.string.notification_text));
        bld.setShowWhen(false);
        bld.setOngoing(true);
        bld.setContentIntent(pendingIntent);
        Notification notification = bld.build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }


    @Override
    public void onPrimaryClipChanged() {
        if(clipboardManager == null)
            return;

        if (!clipboardManager.hasPrimaryClip()) {
            return;
        }
        ClipData cd = clipboardManager.getPrimaryClip();
        if(cd == null)
            return;

        if(cd.getItemCount() == 0)
            return;

        ClipData.Item itemAt = cd.getItemAt(0);
        String data = itemAt.coerceToText(this).toString();

        searchButtonManager.newClipboardText(data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        applicationState.notifyStopped();
        clipboardManager.removePrimaryClipChangedListener(this);

        Logging.SERVICE.i("onDestroy");
        searchButtonManager.destroy();
    }

}