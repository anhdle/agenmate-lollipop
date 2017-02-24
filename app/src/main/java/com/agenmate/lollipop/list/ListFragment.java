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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agenmate.lollipop.R;
import com.agenmate.lollipop.addedit.AddEditActivity;
import com.agenmate.lollipop.addedit.AddEditFragment;
import com.agenmate.lollipop.data.Task;
import com.agenmate.lollipop.ui.layout.SheetLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

import static com.google.common.base.Preconditions.checkNotNull;

public class ListFragment extends Fragment implements ListContract.View  {

    private static final String TAG = "ListFragment";
    protected TasksAdapter tasksAdapter;
    protected LinearLayoutManager layoutManager;
    private ListContract.Presenter presenter;
    private Unbinder unbinder;
    private TextView emptyText;
    private FloatingActionButton fab;
    private SheetLayout sheetLayout;
    public ListFragment() {}

    private int[] colorIds = {R.color.md_red_700, R.color.md_orange_700, R.color.md_yellow_700, R.color.md_green_700, R.color.md_blue_700, R.color.md_indigo_700, R.color.md_deep_purple_700};

    @BindView(R.id.list_view) RecyclerView recyclerView;
    @BindView(R.id.swipe) ScrollChildSwipeRefreshLayout swipeRefreshLayout;


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
        emptyText = ((ListActivity)getActivity()).getEmptyText();
        swipeRefreshLayout.setEnabled(false); // no need since no remote data yet
        //swipeRefreshLayout.setOnRefreshListener(() -> presenter.loadTasks(false));
        //swipeRefreshLayout.setScrollUpChild(recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        fab = ((ListActivity)getActivity()).getFab();
        sheetLayout = ((ListActivity)getActivity()).getSheetLayout();

        fab.setOnClickListener(view -> {
            sheetLayout.setColor(ContextCompat.getColor(getActivity(), colorIds[0]));
            sheetLayout.setFab(fab);
            sheetLayout.setFabAnimationEndListener(new SheetLayout.OnAnimationListener() {
                @Override
                public void onContractAnimationStart() {

                }

                @Override
                public void onContractAnimationEnd() {
                    ((ListActivity)getActivity()).setStatusBarColor(0);
                }

                @Override
                public void onExpandAnimationEnd() {
                    showAddTask();
                }

                @Override
                public void onExpandAnimationStart() {

                }
            });
            fab.setImageDrawable(null);
            sheetLayout.expandFab();
        });

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

        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(active));
    }

    @Override
    public void showTasks(List<Task> tasks, boolean notify) {
        emptyText.setVisibility(View.GONE);
        tasksAdapter.replaceData(tasks, notify);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){
            int color = data.getIntExtra("color", 0);
            sheetLayout.setColor(ContextCompat.getColor(getActivity(), colorIds[color]));
            ((ListActivity)getActivity()).setStatusBarColor(color);
        }
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
        emptyText.setVisibility(View.VISIBLE);
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

    private TaskItemListener taskItemListener = new TaskItemListener() {
        @Override
        public void onTaskClick(Task clickedTask, ImageView view, int color) {

            sheetLayout.setColor(ContextCompat.getColor(getActivity(), colorIds[color]));
            sheetLayout.setFab(view);
            sheetLayout.setFabAnimationEndListener(new SheetLayout.OnAnimationListener() {
                @Override
                public void onContractAnimationStart() {

                }

                @Override
                public void onContractAnimationEnd() {
                    ((ListActivity)getActivity()).setStatusBarColor(0);
                }

                @Override
                public void onExpandAnimationEnd() {
                    presenter.openTaskDetails(clickedTask);
                }

                @Override
                public void onExpandAnimationStart() {
                    ((ListActivity)getActivity()).setStatusBarColor(color);
                }
            });

            sheetLayout.expandFab();

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

}
