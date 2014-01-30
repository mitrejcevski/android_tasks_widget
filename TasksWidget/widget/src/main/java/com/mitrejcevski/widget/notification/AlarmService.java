package com.mitrejcevski.widget.notification;

import android.content.Intent;

/**
 * Service that is performing an action to notify the user about an event happening.
 *
 * @author jovche.mitrejchevski
 */
public class AlarmService extends WakeIntentService {

    /**
     * The name of the notification id in the intent extras.
     */
    public static final String NOTIFICATION_ID = "notification_id";
    /**
     * The nae of the notification title passed in the extras.
     */
    public static final String NOTIFICATION_TITLE = "notification_title";
    /**
     * The name of this service.
     */
    private static final String SERVICE_NAME = "AlarmService";

    /**
     * Constructor.
     */
    public AlarmService() {
        super(SERVICE_NAME);
    }

    @Override
    void doReminderWork(Intent intent) {
        final int id = intent.getIntExtra(NOTIFICATION_ID, -1);
        final String title = intent.getStringExtra(NOTIFICATION_TITLE);
        UserNotification.INSTANCE.showSyncDoneNotification(this, id, title);
    }
}
