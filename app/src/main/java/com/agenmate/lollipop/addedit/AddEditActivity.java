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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.test.espresso.IdlingResource;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
    public static final int REQUEST_EDIT_TASK = 2;
    private AddEditFragment addEditFragment;
    private Menu menu;
    private MenuItem alarmIcon;
    private String taskId;

    private int[] colorBarIds = {R.color.md_red_700, R.color.md_orange_700, R.color.md_yellow_700, R.color.md_green_700, R.color.md_blue_700, R.color.md_indigo_700, R.color.md_deep_purple_700};

    @Inject AddEditPresenter mAddEditTasksPresenter;
    @Inject BaseAlarmController alarmController;
    @BindView(R.id.appbar) AppBarLayout appBar;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        // Set up the toolbar.
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        enterFromBottomAnimation();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        addEditFragment = (AddEditFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        taskId = getIntent().getStringExtra(AddEditFragment.ARGUMENT_EDIT_TASK_ID);

        if (addEditFragment == null) {
            addEditFragment = AddEditFragment.newInstance();

            if (getIntent().hasExtra(AddEditFragment.ARGUMENT_EDIT_TASK_ID)) {
                Bundle bundle = new Bundle();
                bundle.putString(AddEditFragment.ARGUMENT_EDIT_TASK_ID, taskId);
                addEditFragment.setArguments(bundle);
            }

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), addEditFragment, R.id.content_frame);
        }


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

    //private boolean isWhiteText;
    @SuppressLint("NewApi")
    public void setBarColor(int color, boolean isWhiteText){
        appBar.setBackgroundColor(ContextCompat.getColor(this, colorBarIds[color]));
        String title = taskId == null ? "New Task" : "Edit Task";
        toolbar.setTitleTextColor(isWhiteText ? Color.WHITE : Color.BLACK);
        toolbar.setTitle(title);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(isWhiteText ? Color.WHITE : Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        setStatusBarColor(color);

        if(menu != null){
            for(int i = 0; i < menu.size(); i++){
                Drawable drawable = menu.getItem(i).getIcon();
                if(drawable != null) {
                    drawable.mutate();
                    drawable.setColorFilter(isWhiteText ? Color.WHITE : Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                }
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_edit_actions, menu);
        this.menu = menu;
        alarmIcon = menu.findItem(R.id.action_alarm);
        alarmIcon.setVisible(false);
        if(taskId == null) menu.findItem(R.id.action_delete).setVisible(false);
        addEditFragment.onOptionsCreated();

        return super.onCreateOptionsMenu(menu);
    }

}
