package com.agenmate.lollipop.list;

import com.agenmate.lollipop.data.Task;

/**
 * Created by kincaid on 2/20/17.
 */

public interface TaskItemListener {

    void onTaskClick(Task clickedTask);

    void onTaskDelete(Task deletedTask);

    void onCompleteTaskClick(Task completedTask);

    void onActivateTaskClick(Task activatedTask);
}