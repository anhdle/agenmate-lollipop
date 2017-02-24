package com.agenmate.lollipop.source;

import android.content.Context;

import com.agenmate.lollipop.data.source.Local;
import com.agenmate.lollipop.data.source.TasksDataSource;
import com.agenmate.lollipop.data.source.local.TasksLocalDataSource;
import com.agenmate.lollipop.util.schedulers.BaseSchedulerProvider;
import com.agenmate.lollipop.util.schedulers.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TasksRepositoryModule {

    @Singleton
    @Provides
    @Local
    TasksDataSource provideTasksLocalDataSource(Context context) {
        return new TasksLocalDataSource(context, provideSchedulerProvider());
    }

    @Singleton
    @Provides
    BaseSchedulerProvider provideSchedulerProvider() {
        return new SchedulerProvider();
    }

}
