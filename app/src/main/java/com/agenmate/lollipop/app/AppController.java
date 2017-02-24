package com.agenmate.lollipop.app;

import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.agenmate.lollipop.alarm.AlarmModule;
import com.agenmate.lollipop.data.source.DaggerTasksRepositoryComponent;
import com.agenmate.lollipop.data.source.TasksRepositoryComponent;

import net.danlew.android.joda.JodaTimeAndroid;

public class AppController extends MultiDexApplication {

    private TasksRepositoryComponent component;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
       // AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //needs to run once to generate it
        component = DaggerTasksRepositoryComponent.builder()
                .appModule(new AppModule(this))
                .alarmModule(new AlarmModule())
                .build();
    }


    public TasksRepositoryComponent getTasksRepositoryComponent() {
        return component;
    }

}
