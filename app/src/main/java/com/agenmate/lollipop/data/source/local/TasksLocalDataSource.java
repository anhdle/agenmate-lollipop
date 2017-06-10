/*
 * Copyright 2016, The Android Open Source Project
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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.agenmate.lollipop.data.Task;
import com.agenmate.lollipop.data.source.TasksDataSource;
import com.agenmate.lollipop.data.source.local.TasksPersistenceContract.TaskEntry;
import com.d8xo.filling.schedulers.BaseSchedulerProvider;
import com.d8xo.filling.sqlbrite2.BriteDatabase;

import java.util.List;

import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
public class TasksLocalDataSource implements TasksDataSource {


    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private Function<Cursor, Task> taskMapperFunction;

    public TasksLocalDataSource(@NonNull Context context, @NonNull BriteDatabase briteDatabase) {
        checkNotNull(context);

        this.briteDatabase = checkNotNull(briteDatabase);

        taskMapperFunction = this::getTask;
    }

    @NonNull
    private Task getTask(@NonNull Cursor c) {
        String itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
        String title = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
        String description = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
        int priority = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_PRIORITY));
        int color = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COLOR));
        long dueAt = c.getLong(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DUE_AT));
        boolean hasAlarm = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_HAS_ALARM)) == 1;
        boolean completed = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
        return new Task(title, description, priority, color, dueAt, hasAlarm, itemId, completed);
    }

    @Override
    public Observable<List<Task>> getTasks() {
        String[] projection = {
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_PRIORITY,
                TaskEntry.COLUMN_NAME_COLOR,
                TaskEntry.COLUMN_NAME_DUE_AT,
                TaskEntry.COLUMN_NAME_HAS_ALARM,
                TaskEntry.COLUMN_NAME_COMPLETED
        };
        String sql = String.format("SELECT %s FROM %s", TextUtils.join(",", projection), TaskEntry.TABLE_NAME);
        return briteDatabase.createQuery(TaskEntry.TABLE_NAME, sql)
                .mapToList(taskMapperFunction);
    }

    @Override
    public Observable<Task> getTask(@NonNull String taskId) {
        String[] projection = {
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_PRIORITY,
                TaskEntry.COLUMN_NAME_COLOR,
                TaskEntry.COLUMN_NAME_DUE_AT,
                TaskEntry.COLUMN_NAME_HAS_ALARM,
                TaskEntry.COLUMN_NAME_COMPLETED
        };
        String sql = String.format("SELECT %s FROM %s WHERE %s LIKE ?",
                TextUtils.join(",", projection), TaskEntry.TABLE_NAME, TaskEntry.COLUMN_NAME_ENTRY_ID);
        return briteDatabase.createQuery(TaskEntry.TABLE_NAME, sql, taskId)
                .mapToOneOrDefault(taskMapperFunction, null);
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_ENTRY_ID, task.getId());
        values.put(TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskEntry.COLUMN_NAME_DESCRIPTION, task.getDescription());
        values.put(TaskEntry.COLUMN_NAME_PRIORITY, task.getPriority());
        values.put(TaskEntry.COLUMN_NAME_COLOR, task.getColor());
        values.put(TaskEntry.COLUMN_NAME_DUE_AT, task.getDueAt());
        values.put(TaskEntry.COLUMN_NAME_HAS_ALARM, task.hasAlarm());
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, task.isCompleted());
        briteDatabase.insert(TaskEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        completeTask(task.getId());
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, true);

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        briteDatabase.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public void activateTask(@NonNull Task task) {
        activateTask(task.getId());
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, false);

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        briteDatabase.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public void clearCompletedTasks() {
        String selection = TaskEntry.COLUMN_NAME_COMPLETED + " LIKE ?";
        String[] selectionArgs = {"1"};
        briteDatabase.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllTasks() {
        briteDatabase.delete(TaskEntry.TABLE_NAME, null);
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        briteDatabase.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

}
