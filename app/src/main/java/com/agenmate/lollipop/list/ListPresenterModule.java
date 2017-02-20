package com.agenmate.lollipop.list;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link ListPresenter}.
 */
@Module
public class ListPresenterModule {

    private final ListContract.View mView;

    public ListPresenterModule(ListContract.View view) {
        mView = view;
    }

    @Provides
    ListContract.View provideTasksContractView() {
        return mView;
    }

}
