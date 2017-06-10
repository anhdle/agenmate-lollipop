/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agenmate.lollipop.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.d8xo.filling.schedulers.BaseSchedulerProvider;
import com.d8xo.filling.sqlbrite2.BriteDatabase;
import com.d8xo.filling.sqlbrite2.SqlBrite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

@Module
public final class DbModule {

    @Provides
    @Singleton SQLiteOpenHelper provideOpenHelper(Context context) {
        return new TasksOpenHelper(context);
    }

    @Provides
    @Singleton
    SqlBrite provideSqlBrite() {
        return new SqlBrite.Builder()
            .logger(message -> Timber.tag("Database").v(message))
            .build();
    }

    @Provides
    @Singleton
    BriteDatabase provideDatabase(SqlBrite sqlBrite, SQLiteOpenHelper helper, BaseSchedulerProvider schedulerProvider) {
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(helper, schedulerProvider.io());
        db.setLoggingEnabled(true);
        return db;
    }
}
