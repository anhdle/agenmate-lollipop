package com.agenmate.lollipop.list;

import com.agenmate.lollipop.app.AppController;
import com.agenmate.lollipop.data.source.TasksRepositoryComponent;
import com.agenmate.lollipop.util.FragmentScoped;

import dagger.Component;

/**
 * This is a Dagger component. Refer to {@link AppController} for the list of Dagger components
 * used in this application.
 * <P>
 * Because this component depends on the {@link TasksRepositoryComponent}, which is a singleton, a
 * scope must be specified. All fragment components use a custom scope for this purpose.
 */
@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = ListPresenterModule.class)
public interface ListComponent {
	
    void inject(ListActivity activity);
}
