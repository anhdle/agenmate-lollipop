package com.agenmate.lollipop.util;

import org.joda.time.DateTime;

/**
 * Created by kincaid on 2/17/17.
 */

public class TimeUtils {

    public static int getMaxDayOfMonth(int year, int month) {
        DateTime dateTime = new DateTime(year, month, 1, 0, 0, 0, 000);
        return dateTime.dayOfMonth().getMaximumValue();
    }

    public static int getDayOfWeek(int year, int month, int day) {
        DateTime dateTime = new DateTime(year, month, day, 0, 0, 0, 000);
        return dateTime.getDayOfWeek() % 7;
    }
}
