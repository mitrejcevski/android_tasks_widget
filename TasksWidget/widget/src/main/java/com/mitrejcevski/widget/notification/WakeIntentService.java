package com.mitrejcevski.widget.notification;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public abstract class WakeIntentService extends IntentService {

    private static final String WAKE_LOCK_NAME = "com.mitrejcevski.mytasks.static";
    private static PowerManager.WakeLock wakeLock = null;

    public WakeIntentService(String name) {
        super(name);
    }

    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (wakeLock == null) {
            PowerManager powManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = powManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_NAME);
            wakeLock.setReferenceCounted(true);
        }
        return (wakeLock);
    }

    abstract void doReminderWork(Intent intent);

    @Override
    final protected void onHandleIntent(Intent intent) {
        try {
            doReminderWork(intent);
        } finally {
            getLock(this).release();
        }
    }
}
