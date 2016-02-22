package com.mitrejcevski.widget.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.mitrejcevski.widget.R;

public enum UserNotification {

    INSTANCE;

    private NotificationManager notificationManager;

    public void showSyncDoneNotification(final Context context, final int id, final String title) {
        Notification notification = buildCancelNotification(context, title);
        notification.defaults |= Notification.DEFAULT_ALL;
        getNotificationManager(context).notify(id, notification);
    }

    private Notification buildCancelNotification(final Context context, final String title) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.applicationName))
                .setAutoCancel(true)
                .setContentText(title);
        return builder.build();
    }

    private NotificationManager getNotificationManager(final Context context) {
        if (notificationManager == null)
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;
    }
}
