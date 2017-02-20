package com.agenmate.lollipop.alarm;

import android.content.Context;

import com.agenmate.lollipop.data.source.TasksDataSource;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by kincaid on 2/2/17.
 */

@Module
public class AlarmModule {

    @Singleton
    @Provides
    BaseAlarmController provideAlarmController(Context context){
        return new AlarmController(context, provideNextAlarms());
    }

    @Singleton
    @Provides
    List<Alarm> provideNextAlarms() {
        return new LinkedList<>();
    }
}
