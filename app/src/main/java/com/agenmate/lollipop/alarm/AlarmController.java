package com.agenmate.lollipop.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import com.agenmate.lollipop.addedit.AddEditActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AlarmController implements BaseAlarmController {

    private Context context;
    private List<Alarm> alarms;
    private AlarmManager alarmManager;

    //Temp
    public boolean isActive;

    public AlarmController(Context context, List<Alarm> alarms){
        this.context = context;
        this.alarms = alarms;
        this.alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
    }
    @Override
    public void resetAlarm(int minute, int hour) {

        long time = getNextAlarm(minute, hour, false);
        if(alarms.isEmpty() || time < alarms.get(0).getTime()){
          //  alarms.add(0, new Alarm(time));
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("extra", "yes");
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, time, sender);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, sender);
                }
                intent = new Intent("android.intent.action.ALARM_CHANGED");
                intent.putExtra("alarmSet", true);
                context.sendBroadcast(intent);
                SimpleDateFormat fmt = new SimpleDateFormat("E HH:mm");
                Settings.System.putString(context.getContentResolver(),
                        Settings.System.NEXT_ALARM_FORMATTED, fmt.format(time));
            } else {
                Intent showIntent = new Intent(context, AddEditActivity.class);
                PendingIntent showOperation = PendingIntent.getActivity(context, 0, showIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(time, showOperation);
                alarmManager.setAlarmClock(alarmClockInfo, sender);
            }

        } else {
            alarms.add(new Alarm(time));
            //sort, check for unique
        }
        turnonAlarm();
    }

    @Override
    public void dismissAlarm() {
        context.stopService(new Intent(context, AlarmService.class));
    }

    @Override
    public void cancelAlarm() {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(sender);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            intent = new Intent("android.intent.action.ALARM_CHANGED");
            intent.putExtra("alarmSet", false);
            context.sendBroadcast(intent);
            Settings.System.putString(context.getContentResolver(),
                    Settings.System.NEXT_ALARM_FORMATTED, "");
        }

        this.isActive = false;
    }

    @Override
    public void wakeupAlarm() {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl =
                pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "AlarmReceiver");
        wl.acquire(5000);
        context.startService(new Intent(context, AlarmService.class));
    }

    @Override
    public void turnonAlarm() {
        this.isActive = true;
        //do something with alarm
    }

    @Override
    public long getNextAlarm(int minute, int hour, boolean am) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (System.currentTimeMillis() >= calendar.getTimeInMillis()) {
            calendar.add(Calendar.DATE, 1);
        }

        return calendar.getTimeInMillis();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }


}