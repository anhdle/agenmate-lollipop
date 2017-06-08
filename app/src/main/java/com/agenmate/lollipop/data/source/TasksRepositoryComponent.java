package com.agenmate.lollipop.data.source;

import android.content.Context;
import android.content.SharedPreferences;

import com.agenmate.lollipop.alarm.AlarmModule;
import com.agenmate.lollipop.alarm.AlarmReceiver;
import com.agenmate.lollipop.alarm.BaseAlarmController;
import com.agenmate.lollipop.app.AppController;
import com.agenmate.lollipop.app.AppModule;
import com.d8xo.filling.schedulers.BaseSchedulerProvider;


import javax.inject.Singleton;

import dagger.Component;

/**
 * This is a Dagger component. Refer to {@link AppController} for the list of Dagger components
 * used in this application.
 * <P>
 * Even though Dagger allows annotating a {@link Component @Component} as a singleton, the code
 * itself must ensure only one instance of the class is created. This is done in {@link
 * AppController}.
 */
@Singleton
@Component(modules = {TasksRepositoryModule.class, AppModule.class, AlarmModule.class})
public interface TasksRepositoryComponent {

    Context getContext();

    SharedPreferences getSharedPreferences();

    TasksRepository getTasksRepository();

    BaseSchedulerProvider getSchedulerProvider();

    BaseAlarmController getAlarmController();

    void inject(AlarmReceiver alarmReceiver);
}
