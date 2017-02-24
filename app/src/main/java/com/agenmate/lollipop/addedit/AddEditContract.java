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

import com.agenmate.lollipop.alarm.BaseAlarmController;
import com.agenmate.lollipop.base.BasePresenter;
import com.agenmate.lollipop.base.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface AddEditContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTaskError();

        void showTasksList(int returnColor);

        void showMissingTask();

        void setTitle(String title);

        void setDescription(String description);

        void setPriority(int priority);

        void setColor(int color);

        void setDueDate(long dueDate);

        boolean isActive();

        void showTaskDeleted();
    }

    interface Presenter extends BasePresenter {

        void saveTask(String title, String description, int priority, int color, long dueAt, boolean hasAlarm);

        void populateTask();

        boolean isDataMissing();

        void deleteTask();

        BaseAlarmController getAlarmController();
    }
}
