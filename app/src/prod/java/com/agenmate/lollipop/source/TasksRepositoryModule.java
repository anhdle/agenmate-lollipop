package com.agenmate.lollipop.source;

import android.content.Context;

import com.agenmate.lollipop.data.source.Local;
import com.agenmate.lollipop.data.source.Remote;
import com.agenmate.lollipop.data.source.TasksDataSource;
import com.agenmate.lollipop.data.source.TasksRepository;
import com.agenmate.lollipop.data.source.local.TasksLocalDataSource;
import com.agenmate.lollipop.data.source.remote.TasksRemoteDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This is used by Dagger to inject the required arguments into the {@link TasksRepository}.
 */
@Module
public class TasksRepositoryModule {

    @Singleton
    @Provides
    @Local
    TasksDataSource provideTasksLocalDataSource(Context context) {
        return new TasksLocalDataSource(context);
    }

    @Singleton
    @Provides
    @Remote
    TasksDataSource provideTasksRemoteDataSource() {
        return new TasksRemoteDataSource();
    }

}
