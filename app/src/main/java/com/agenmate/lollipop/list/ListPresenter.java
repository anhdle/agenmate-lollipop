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

package com.agenmate.lollipop.list;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.agenmate.lollipop.addedit.AddEditActivity;
import com.agenmate.lollipop.data.Task;
import com.agenmate.lollipop.data.source.TasksRepository;
import com.agenmate.lollipop.util.EspressoIdlingResource;
import com.d8xo.filling.schedulers.BaseSchedulerProvider;
import com.google.common.base.Strings;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Listens to user actions from the UI ({@link ListFragment}), retrieves the data and updates the
 * UI as required.
 * <p />
 * By marking the constructor with {@code @Inject}, Dagger injects the dependencies required to
 * create an instance of the TasksPresenter (if it fails, it emits a compiler error).  It uses
 * {@link ListPresenterModule} to do so.
 * <p/>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 **/
final class ListPresenter implements ListContract.Presenter {

    private int returnColor;

    @NonNull
    private final TasksRepository mTasksRepository;

    @NonNull
    private final ListContract.View listView;

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    @NonNull
    private TasksFilterType currentFiltering = TasksFilterType.ALL_TASKS;

    @NonNull
    private CompositeDisposable subscriptions;

    @Inject
    Context context;

    @Inject
    SharedPreferences sharedPreferences;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    ListPresenter(@NonNull TasksRepository tasksRepository,
                  @NonNull ListContract.View tasksView,
                  @NonNull BaseSchedulerProvider schedulerProvider) {
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null");
        listView = checkNotNull(tasksView, "tasksView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");
        subscriptions = new CompositeDisposable();
    }

    /**
     * Method injection is used here to safely reference {@code this} after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    void setupListeners() {
        listView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadTasks(false);
    }

    @Override
    public void unsubscribe() {
        subscriptions.clear();
    }


    @Override
    public void result(int requestCode, int resultCode) {
        if(Activity.RESULT_OK == resultCode){
            this.returnColor = returnColor;
            switch (requestCode){
                case AddEditActivity.REQUEST_ADD_TASK:
                    if(firstLoad){
                        sharedPreferences.edit()
                                .putBoolean("firstLoad", false)
                                .apply();
                    }
                    listView.showSuccessfullySavedMessage(true);
                    break;
                case AddEditActivity.REQUEST_EDIT_TASK:
                    listView.showSuccessfullySavedMessage(false);
                    break;
            }
        }
    }

    private boolean firstLoad;
    @Override
    public void loadTasks(boolean forceUpdate) {
        firstLoad = sharedPreferences
                .getBoolean("firstLoad", true);

        loadTasks(false, true, true);
    }


    private boolean refreshView;
    /**
     * @param forceUpdate   Pass in true to refresh the data in the
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     * @param refreshView
     */
    private void loadTasks(final boolean forceUpdate, final boolean showLoadingUI, boolean refreshView) {

        if (showLoadingUI) {
            listView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mTasksRepository.refreshTasks();
        }

        this.refreshView = refreshView;

        EspressoIdlingResource.increment(); // App is busy until further notice
        subscriptions.clear();
        Disposable subscription = mTasksRepository
                .getTasks()
                .flatMap(new Function<List<Task>, Observable<Task>>() {
                    @Override
                    public Observable<Task> apply(@io.reactivex.annotations.NonNull List<Task> tasks) throws Exception {
                        Log.v("tasksize", String.valueOf(tasks.size()));
                        Log.v("task1", String.valueOf(tasks.get(0).getTitle()));
                        return Observable.fromIterable(tasks);
                    }

                })
                /*.filter(task -> {

                    switch (currentFiltering) {
                        case ACTIVE_TASKS:
                            return task.isActive();
                        case COMPLETED_TASKS:
                            return task.isCompleted();
                        case ALL_TASKS:
                        default:
                            return true;
                    }
                })*/
                /*.toSortedList((task1, task2) -> {
                    Integer priorityValue = Integer.valueOf(task2.getPriority()).compareTo(task1.getPriority());
                    if (priorityValue == 0) {
                        int dueAtValue = Long.valueOf(task1.getDueAt()).compareTo(task2.getDueAt());
                        return dueAtValue;
                    }
                    Log.v("priority", String.valueOf(priorityValue));
                    return priorityValue;

                })*/
                .toList()
                .toObservable()
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .doOnNext(new Consumer<List<Task>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<Task> tasks)  {
                        Log.v("process", String.valueOf(tasks));
                        processTasks(tasks);
                    }
                })
                .doOnError(throwable -> listView.showLoadingTasksError())
                .doOnComplete(() -> listView.setLoadingIndicator(false))
                .subscribe();

                /*.doAfterTerminate(() -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                        EspressoIdlingResource.decrement(); // Set app as idle.
                    }
                })*/

                        /*subscribe(this::processTasks,
                        throwable -> listView.showLoadingTasksError(),
                        () -> listView.setLoadingIndicator(false));*/
        subscriptions.add(subscription);
    }

    private void processTasks(List<Task> tasks) {
        Log.v("taskempty?", String.valueOf(tasks.isEmpty()));
        if (tasks.isEmpty()) {
            processEmptyTasks();
        } else {
            listView.showTasks(tasks, refreshView);
            refreshView = true;
            //showFilterLabel();
        }
    }

    private void showFilterLabel() {
        switch (currentFiltering) {
            case ACTIVE_TASKS:
                listView.showActiveFilterLabel();
                break;
            case COMPLETED_TASKS:
                listView.showCompletedFilterLabel();
                break;
            default:
                listView.showAllFilterLabel();
                break;
        }
    }

    private void processEmptyTasks() {
        switch (currentFiltering) {
            case ACTIVE_TASKS:
                listView.showNoActiveTasks();
                break;
            case COMPLETED_TASKS:
                listView.showNoCompletedTasks();
                break;
            default:
                sharedPreferences.edit()
                        .putBoolean("firstLoad", true)
                        .apply();
                listView.showNoTasks();
                break;
        }
    }

    @Override
    public void addNewTask() {
        listView.showAddTask();
    }

    @Override
    public void openTaskDetails(@NonNull Task requestedTask) {
        checkNotNull(requestedTask, "requestedTask cannot be null!");
        listView.showEditTask(requestedTask.getId());
    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        checkNotNull(completedTask, "completedTask cannot be null!");

        mTasksRepository.completeTask(completedTask);
        listView.showTaskMarkedComplete();
        loadTasks(false, false, false);
    }

    @Override
    public void activateTask(@NonNull Task activeTask) {
        checkNotNull(activeTask, "activeTask cannot be null!");
        mTasksRepository.activateTask(activeTask);
        listView.showTaskMarkedActive();
        loadTasks(false, false, false);
    }

    @Override
    public void deleteTask(String taskId) {
        if (Strings.isNullOrEmpty(taskId)) {
            listView.showMissingTask();
            return;
        }
        mTasksRepository.deleteTask(taskId);
        loadTasks(false);
        listView.showTaskDeleted();
    }

    @Override
    public void clearCompletedTasks() {
        mTasksRepository.clearCompletedTasks();
        listView.showCompletedTasksCleared();
        loadTasks(false, false, true);
    }

    /**
     * Sets the current task filtering type.
     *
     * @param requestType Can be {@link TasksFilterType#ALL_TASKS},
     *                    {@link TasksFilterType#COMPLETED_TASKS}, or
     *                    {@link TasksFilterType#ACTIVE_TASKS}
     */
    @Override
    public void setFiltering(TasksFilterType requestType) {
        currentFiltering = requestType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return currentFiltering;
    }


}
