package com.agenmate.lollipop.data.source;

import android.content.Context;
import com.agenmate.lollipop.data.FakeTasksRemoteDataSource;
import com.agenmate.lollipop.data.source.local.TasksLocalDataSource;
import com.d8xo.filling.schedulers.BaseSchedulerProvider;
import com.d8xo.filling.schedulers.SchedulerProvider;

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
    @Remote
    TasksDataSource provideTasksRemoteDataSource() {
        return new FakeTasksRemoteDataSource();
    }

    @Singleton
    @Provides
    BaseSchedulerProvider provideSchedulerProvider() {
        return new SchedulerProvider();
    }

}
