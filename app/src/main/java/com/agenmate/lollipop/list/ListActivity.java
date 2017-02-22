/*
 * Copyright (C) 2015 The Android Open Source Project
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


import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.test.espresso.IdlingResource;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.agenmate.lollipop.R;
import com.agenmate.lollipop.app.AppController;
import com.agenmate.lollipop.base.BaseActivity;
import com.agenmate.lollipop.ui.layout.SheetLayout;
import com.agenmate.lollipop.util.EspressoIdlingResource;
import com.agenmate.lollipop.util.MarkupUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends BaseActivity {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    public static final String TAG = "MainActivity";

    private DrawerArrowDrawable drawerArrowDrawable;
    private ActionBarDrawerToggle drawerToggle;
    private ListFragment orderedFragment;
    @Inject ListPresenter mListPresenter;
    @BindView(R.id.appbar) AppBarLayout appBar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.sheet) SheetLayout sheetLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.tabs) TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
     //   enterFromBottomAnimation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false); // disable the button
        actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
        actionBar.setDisplayShowHomeEnabled(false); // remove the icon
        actionBar.setTitle(MarkupUtils.fromHtml("<font color=" + "'#ffffff'" +">AgenMate</font>"));
        tabLayout.setVisibility(View.GONE);

        // TODO finish drawerlayout menu later for remote and profile
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); // temp
        /*drawerArrowDrawable = new DrawerArrowDrawable(this);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.setDrawerArrowDrawable(drawerArrowDrawable);*/

        sheetLayout.setFab(fab);
        sheetLayout.setFabAnimationEndListener(new SheetLayout.OnFabAnimationEndListener() {
            @Override
            public void onFabAnimationEnd() {
                orderedFragment.showAddTask();

            }
        });


        fab.setOnClickListener(view -> {
            fab.setImageDrawable(null);
            sheetLayout.expandFab();
        });
        tabLayout.setupWithViewPager(viewPager);

        orderedFragment = ListFragment.newInstance();

        DaggerListComponent.builder()
                .tasksRepositoryComponent(((AppController) getApplication()).getTasksRepositoryComponent())
                .listPresenterModule(new ListPresenterModule(orderedFragment))
                .build()
                .inject(this);

        if (savedInstanceState != null) {
            TasksFilterType currentFiltering =
                    (TasksFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            mListPresenter.setFiltering(currentFiltering);
        }


        setupDrawerContent(navigationView);
        setupViewPager(viewPager);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
            new Handler().postDelayed(() -> sheetLayout.contractFab(), 300);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTERING_KEY, mListPresenter.getFiltering());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(orderedFragment, "Order");

        viewPager.setAdapter(pagerAdapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                });
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }

    private  void startAnimation(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setImageResource(R.drawable.ic_add);
    }

    public FloatingActionButton getFab(){
        return fab;
    }
}
