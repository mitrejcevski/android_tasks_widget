package com.mitrejcevski.widget.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WakeIntentService.acquireStaticLock(context);
        Intent alarmIntent = new Intent(context, AlarmService.class);
        alarmIntent.putExtras(intent.getExtras());
        context.startService(alarmIntent);
    }
}

