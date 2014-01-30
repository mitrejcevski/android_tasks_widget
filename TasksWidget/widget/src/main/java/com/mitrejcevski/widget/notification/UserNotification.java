package com.mitrejcevski.widget.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.mitrejcevski.widget.R;

/**
 * Prepares and shows a notification.
 *
 * @author jovche.mitrejchevski
 */
public enum UserNotification {

    /**
     * Singleton.
     */
    INSTANCE;
    private Notification mNotification;
    private NotificationManager mNotificationManager;

    /**
     * Shows a notification that can be cancelled.
     *
     * @param context
     */
    public void showSyncDoneNotification(final Context context, final int id, final String title) {
        mNotification = buildCancelNotification(context, title);
        mNotification.defaults |= Notification.DEFAULT_ALL;
        getNotificationManager(context).notify(id, mNotification);
    }

    /**
     * Builds the cancelable notification.
     *
     * @param context
     * @param title
     * @return
     */
    private Notification buildCancelNotification(final Context context, final String title) {
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.applicationName))
                .setAutoCancel(true)
                .setContentText(title);
        return builder.build();
    }

    /**
     * Retrieves the notification manager (new instance if null or the instance
     * we have if not).
     *
     * @param context
     * @return
     */
    private NotificationManager getNotificationManager(final Context context) {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }
}
