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

package com.agenmate.lollipop.addedit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.agenmate.lollipop.alarm.BaseAlarmController;
import com.agenmate.lollipop.data.Task;
import com.agenmate.lollipop.data.source.TasksDataSource;
import com.agenmate.lollipop.data.source.TasksRepository;

import com.d8xo.filling.schedulers.BaseSchedulerProvider;
import com.google.common.base.Strings;

import javax.inject.Inject;


import io.reactivex.disposables.CompositeDisposable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link AddEditFragment}), retrieves the data and
 * updates
 * the UI as required.
 * <p />
 * By marking the constructor with {@code @Inject}, Dagger injects the dependencies required to
 * create an instance of the AddEditTaskPresenter (if it fails, it emits a compiler error). It uses
 * {@link AddEditPresenterModule} to do so.
 * <p/>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually bypassing Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 */
final class AddEditPresenter implements AddEditContract.Presenter {

    @NonNull
    private final TasksDataSource tasksRepository;

    @NonNull
    private final AddEditContract.View addEditView;

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    @Nullable
    private String taskId;

    private boolean mIsDataMissing;

    @NonNull
    private CompositeDisposable disposables;

    private BaseAlarmController alarmController;


    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    AddEditPresenter(@Nullable String taskId,
                     @NonNull TasksRepository tasksRepository,
                     @NonNull AddEditContract.View addEditView,
                     @NonNull BaseSchedulerProvider schedulerProvider,
                     @NonNull BaseAlarmController alarmController) {
        this.taskId = taskId;
        this.tasksRepository = checkNotNull(tasksRepository);
        this.addEditView = checkNotNull(addEditView);
        mSchedulerProvider = checkNotNull(schedulerProvider);
        this.alarmController = checkNotNull(alarmController);
        mIsDataMissing = true;
        disposables = new CompositeDisposable();
    }

    /**
     * Method injection is used here to safely reference {@code this} after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    void setupListeners() {
        addEditView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        if (!isNewTask() && mIsDataMissing) {
            populateTask();
        } else {
            addEditView.setPriority(0);
            addEditView.setColor(0);
            addEditView.setDueDate(0);
        }
    }

    @Override
    public void unsubscribe() {
        disposables.clear();
    }


    @Override
    public void saveTask(String title, String description, int priority, int color, long dueAt, boolean hasAlarmc, boolean isCompleted) {
        if (isNewTask()) {
            createTask(title, description, priority, color, dueAt, hasAlarmc);
        } else {
            updateTask(title, description, priority, color, dueAt, hasAlarmc, isCompleted);
        }
    }


    @Override
    public void populateTask() {
        if (isNewTask()) {
            throw new RuntimeException("populateTask() was called but task is new.");
        }

        disposables.add(tasksRepository
                .getTask(taskId)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(
                        // onNext
                        task -> {
                            if (addEditView.isAdded()) {
                                addEditView.setTitle(task.getTitle()); // TODO
                                addEditView.setDescription(task.getDescription());
                                addEditView.setPriority(task.getPriority());
                                addEditView.setColor(task.getColor());
                                addEditView.setDueDate(task.getDueAt());
                                addEditView.setCompleted(task.isCompleted());
                                mIsDataMissing = false;
                            }
                        }, // onError
                        __ -> {
                            if (addEditView.isAdded()) {
                                addEditView.showEmptyTaskError();
                            }
                        }));
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    private boolean isNewTask() {
        return taskId == null;
    }

    private void createTask(String title, String description, int priority, int color, long dueAt, boolean hasAlarm) {
        Task newTask = new Task(title, description, priority, color, dueAt, hasAlarm);
        if (newTask.isEmpty()) {
            addEditView.showEmptyTaskError();
        } else {
            tasksRepository.saveTask(newTask);
            addEditView.showTasksList(color);
        }
    }

    private void updateTask(String title, String description, int priority, int color, long dueAt, boolean hasAlarm, boolean isCompleted) {
        if (isNewTask()) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        Task task = new Task(title, description, priority, color, dueAt, hasAlarm, taskId);
        task.setCompleted(isCompleted);
        tasksRepository.saveTask(task);
        addEditView.showTasksList(color);
    }

    public BaseAlarmController getAlarmController() {
        return alarmController;
    }

    @Override
    public void deleteTask() {
        if (Strings.isNullOrEmpty(taskId)) {
            addEditView.showMissingTask();
            return;
        }
        tasksRepository.deleteTask(taskId);
        addEditView.showTaskDeleted();
    }
}
