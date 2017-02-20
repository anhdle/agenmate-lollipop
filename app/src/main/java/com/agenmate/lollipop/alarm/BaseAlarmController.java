package com.agenmate.lollipop.alarm;

import android.content.Intent;

/**
 * Created by kincaid on 2/2/17.
 */

public interface BaseAlarmController {

    void resetAlarm(int minute, int hour);
    void dismissAlarm();
    void cancelAlarm();
    void wakeupAlarm();
    void turnonAlarm();
    long getNextAlarm(int minute, int hour, boolean am);

    // temp
    boolean isActive();

}
