package com.agenmate.lollipop.data.source;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;

import com.agenmate.lollipop.alarm.AlarmModule;
import com.agenmate.lollipop.alarm.AlarmReceiver;
import com.agenmate.lollipop.alarm.BaseAlarmController;
import com.agenmate.lollipop.app.AppController;
import com.agenmate.lollipop.app.AppModule;
import com.agenmate.lollipop.data.source.local.DbModule;
import com.d8xo.filling.schedulers.BaseSchedulerProvider;
import com.d8xo.filling.sqlbrite2.BriteDatabase;
import com.d8xo.filling.sqlbrite2.SqlBrite;


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
@Component(modules = {TasksRepositoryModule.class, AppModule.class, DbModule.class, AlarmModule.class})
public interface TasksRepositoryComponent {

    Context getContext();

    SharedPreferences getSharedPreferences();

    TasksRepository getTasksRepository();

    BaseSchedulerProvider getSchedulerProvider();

    BriteDatabase getBriteDatabase();

    BaseAlarmController getAlarmController();

    void inject(AlarmReceiver alarmReceiver);
}
