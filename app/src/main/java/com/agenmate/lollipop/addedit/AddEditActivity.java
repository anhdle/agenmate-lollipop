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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.test.espresso.IdlingResource;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.agenmate.lollipop.R;
import com.agenmate.lollipop.alarm.BaseAlarmController;
import com.agenmate.lollipop.app.AppController;
import com.agenmate.lollipop.base.BaseActivity;
import com.agenmate.lollipop.util.ActivityUtils;
import com.agenmate.lollipop.util.EspressoIdlingResource;
import com.agenmate.lollipop.util.MarkupUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays an add or edit task screen.
 */
public class AddEditActivity extends BaseActivity {

    public static final int REQUEST_ADD_TASK = 1;
    private AddEditFragment addEditFragment;

    @Inject AddEditPresenter mAddEditTasksPresenter;
    @Inject BaseAlarmController alarmController;
    @BindView(R.id.appbar) AppBarLayout appBar;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private ActionBar actionBar;

    private int[] colorBarIds = {R.color.md_red_700, R.color.md_orange_700, R.color.md_yellow_700, R.color.md_green_700, R.color.md_blue_700, R.color.md_indigo_700, R.color.md_deep_purple_700};
    private int[] colorStatusIds = {R.color.md_red_900, R.color.md_orange_900, R.color.md_yellow_900, R.color.md_green_900, R.color.md_blue_900, R.color.md_indigo_900, R.color.md_deep_purple_900};

    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Set up the toolbar.
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        addEditFragment = (AddEditFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        String taskId = getIntent().getStringExtra(AddEditFragment.ARGUMENT_EDIT_TASK_ID);

        if (addEditFragment == null) {
            addEditFragment = AddEditFragment.newInstance();

            if (getIntent().hasExtra(AddEditFragment.ARGUMENT_EDIT_TASK_ID)) {
                actionBar.setTitle(R.string.edit_task);
                Bundle bundle = new Bundle();
                bundle.putString(AddEditFragment.ARGUMENT_EDIT_TASK_ID, taskId);
                addEditFragment.setArguments(bundle);
            } else {
                actionBar.setTitle(R.string.add_task);
            }

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), addEditFragment, R.id.content_frame);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }



        // Create the presenter
        DaggerAddEditComponent.builder()
                .addEditPresenterModule(
                        new AddEditPresenterModule(addEditFragment, taskId))
                .tasksRepositoryComponent(
                        ((AppController) getApplication()).getTasksRepositoryComponent()).build()
                .inject(this);
    }


    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }

    @SuppressLint("NewApi")
    public void setTabColor(int color){
        appBar.setBackgroundColor(ContextCompat.getColor(this, colorBarIds[color]));
        String htmlColor = color == 0 || color == 5 || color == 6 ? "'#ffffff'" : "'#000000'";
        actionBar.setTitle(MarkupUtils.fromHtml("<font color=" + htmlColor +">New TODO</font>"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, colorStatusIds[color]));
        }

    }
}
