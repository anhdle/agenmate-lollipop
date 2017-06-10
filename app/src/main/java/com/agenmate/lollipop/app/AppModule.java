package com.agenmate.lollipop.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.d8xo.filling.schedulers.BaseSchedulerProvider;
import com.d8xo.filling.schedulers.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by kincaid on 1/3/17.
 */

@Module
public final class AppModule {

    private final Context context;
    private final SharedPreferences sharedPreferences;

    AppModule(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return sharedPreferences;
    }

    @Singleton
    @Provides
    BaseSchedulerProvider provideSchedulerProvider() {
        return new SchedulerProvider();
    }


}