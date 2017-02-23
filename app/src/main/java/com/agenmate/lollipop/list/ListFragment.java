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


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agenmate.lollipop.R;
import com.agenmate.lollipop.addedit.AddEditActivity;
import com.agenmate.lollipop.addedit.AddEditFragment;
import com.agenmate.lollipop.data.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Display a grid of {@link Task}s. User can choose to view all, active or completed tasks.
 */
public class ListFragment extends Fragment implements ListContract.View  {

    private static final String TAG = "ListFragment";

    // TODO disbale loading
    protected TasksAdapter tasksAdapter;
    protected LinearLayoutManager layoutManager;
    private ListContract.Presenter presenter;
    private Unbinder unbinder;
    public ListFragment() {}

    private FloatingActionButton fab;

    @BindView(R.id.list_view) RecyclerView recyclerView;
    @BindView(R.id.swipe) ScrollChildSwipeRefreshLayout swipeRefreshLayout;

    /**
     * Listener for clicks on tasks in the ListView.
     */
    TaskItemListener taskItemListener = new TaskItemListener() {
        @Override
        public void onTaskClick(Task clickedTask) {
            presenter.openTaskDetails(clickedTask);
        }

        @Override
        public void onTaskDelete(Task deletedTask) {
            presenter.deleteTask(deletedTask.getId());
        }

        @Override
        public void onCompleteTaskClick(Task completedTask) { presenter.completeTask(completedTask); }

        @Override
        public void onActivateTaskClick(Task activatedTask) { presenter.activateTask(activatedTask); }
    };

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        rootView.setTag(TAG);
        unbinder = ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setEnabled(false); // TODO fix missing tasks on swipe
        //swipeRefreshLayout.setOnRefreshListener(() -> presenter.loadTasks(false));
        //swipeRefreshLayout.setScrollUpChild(recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        fab = ((ListActivity)getActivity()).getFab();

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(tasksAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    fab.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 ||dy < 0 && fab.isShown()) {
                    fab.hide();
                }
            }
        });

        recyclerView.setItemAnimator(new LandingAnimator());



        return rootView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tasksAdapter = new TasksAdapter(getActivity(), new ArrayList<>(0), taskItemListener);
        setHasOptionsMenu(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    public void setPresenter(@NonNull ListContract.Presenter presenter) {
        this.presenter = checkNotNull(presenter);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void setLoadingIndicator(boolean active) {
        if (getView() == null) {
            return;
        }

        // Make sure setRefreshing() is called after the layout is done with everything else.
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void showTasks(List<Task> tasks, boolean notify) {
        tasksAdapter.replaceData(tasks, notify);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.result(requestCode, resultCode);
    }

    @Override
    public void showAddTask() {
        Intent intent = new Intent(getActivity(), AddEditActivity.class);
        startActivityForResult(intent, AddEditActivity.REQUEST_ADD_TASK);
    }

    @Override
    public void showEditTask(String taskId) {
        Intent intent = new Intent(getContext(), AddEditActivity.class);
        intent.putExtra(AddEditFragment.ARGUMENT_EDIT_TASK_ID, taskId);
        startActivityForResult(intent, AddEditActivity.REQUEST_EDIT_TASK);
    }

    @Override
    public void showTaskMarkedComplete() {
        showMessage("Task Completed!");
    }

    @Override
    public void showTaskMarkedActive() {
        showMessage("Task Active!");
    }

    @Override
    public void showTaskDeleted() {
        showMessage("Task Deleted!");
    }

    @Override
    public void showCompletedTasksCleared() {
        showMessage("All Completed Tasks Cleared!");
    }

    @Override
    public void showLoadingTasksError(){
        showMessage("Oops!");
    }

    @Override
    public void showNoTasks() {
       tasksAdapter.clear();
    }

    @Override
    public void showActiveFilterLabel() {

    }

    @Override
    public void showCompletedFilterLabel() {

    }

    @Override
    public void showAllFilterLabel() {

    }

    @Override
    public void showNoActiveTasks() {

    }

    @Override
    public void showNoCompletedTasks() {

    }

    @Override
    public void showSuccessfullySavedMessage(boolean isNew) {
        showMessage(isNew ? "New Task Added!" : "Task Updated!");
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showFilteringPopUpMenu() {

    }

    @Override
    public void showMissingTask() {

    }

    private void showMessage(String message){
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }
}
