package com.agenmate.lollipop.detail;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link DetailPresenter}.
 */
@Module
public class DetailPresenterModule {

    private final DetailContract.View mView;

    private final String mTaskId;

    public DetailPresenterModule(DetailContract.View view, String taskId) {
        mView = view;
        mTaskId = taskId;
    }

    @Provides
    DetailContract.View provideTaskDetailContractView() {
        return mView;
    }

    @Provides
    String provideTaskId() {
        return mTaskId;
    }
}
