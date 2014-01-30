package com.mitrejcevski.widget.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver that is listening on alarm events.
 *
 * @author jovche.mitrejchevski
 */
public class OnAlarmReceiver extends BroadcastReceiver {

    /**
     * Called when the receiver receives a broadcast message.
     *
     * @param context Context
     * @param intent  Calling intent.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        WakeIntentService.acquireStaticLock(context);
        Intent alarmIntent = new Intent(context, AlarmService.class);
        alarmIntent.putExtras(intent.getExtras());
        context.startService(alarmIntent);
    }
}

