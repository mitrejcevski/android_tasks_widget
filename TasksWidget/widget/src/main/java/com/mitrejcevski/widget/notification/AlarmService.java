package com.mitrejcevski.widget.notification;

import android.content.Intent;

public class AlarmService extends WakeIntentService {

    public static final String NOTIFICATION_ID = "notification_id";
    public static final String NOTIFICATION_TITLE = "notification_title";

    private static final String SERVICE_NAME = "AlarmService";

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
