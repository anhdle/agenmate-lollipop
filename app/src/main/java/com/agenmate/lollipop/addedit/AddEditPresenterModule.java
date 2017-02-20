package com.agenmate.lollipop.addedit;

import android.support.annotation.Nullable;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link AddEditPresenter}.
 */
@Module
public class AddEditPresenterModule {

    private final AddEditContract.View mView;

    private String mTaskId;

    public AddEditPresenterModule(AddEditContract.View view, @Nullable String taskId) {
        mView = view;
        mTaskId = taskId;
    }

    @Provides
    AddEditContract.View provideAddEditTaskContractView() {
        return mView;
    }

    @Provides
    @Nullable
    String provideTaskId() {
        return mTaskId;
    }
}
