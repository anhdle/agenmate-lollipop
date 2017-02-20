package com.agenmate.lollipop.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.agenmate.lollipop.app.AppController;

/**
 * Created by kincaid on 1/31/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ((AppController)(context.getApplicationContext()))
            .getTasksRepositoryComponent().getAlarmController()
            .wakeupAlarm();
    }
}
