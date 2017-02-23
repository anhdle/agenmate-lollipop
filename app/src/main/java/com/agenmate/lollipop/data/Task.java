package com.agenmate.lollipop.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

/**
 * Created by kincaid on 12/31/16.
 */


public final class Task {

    @NonNull
    private final String mId;

    @NonNull
    private final String mTitle;

    @Nullable
    private final String mDescription;

    @NonNull
    private final int priority;

    @NonNull
    private final int color;

    @NonNull
    private final long dueAt;

    @NonNull
    private final boolean hasAlarm;

    private boolean isCompleted;

    /**
     * Use this constructor to create a new active Task.
     *
     * @param title       title of the task
     * @param description description of the task
     */
    public Task(@NonNull String title, @Nullable String description, @NonNull int priority, @NonNull int color, @NonNull long dueAt, @NonNull boolean hasAlarm) {
        this(title, description, priority, color, dueAt, hasAlarm, UUID.randomUUID().toString(), false);
    }

    /**
     * Use this constructor to create an active Task if the Task already has an id (copy of another
     * Task).
     *
     * @param title       title of the task
     * @param description description of the task
     * @param id          id of the task
     */
    public Task(@NonNull String title, @Nullable String description, @NonNull int priority, @NonNull int color, @NonNull long dueAt, @NonNull boolean hasAlarm, @NonNull String id) {
        this(title, description, priority, color, dueAt, hasAlarm, id, false);
    }

    /**
     * Use this constructor to create a new completed Task.
     *
     * @param title       title of the task
     * @param description description of the task
     * @param completed   true if the task is completed, false if it's active
     */
    public Task(@NonNull String title, @Nullable String description, @NonNull int priority, @NonNull int color, @NonNull long dueAt, @NonNull boolean hasAlarm, boolean completed) {
        this(title, description, priority, color, dueAt, hasAlarm, UUID.randomUUID().toString(), completed);
    }

    /**
     * Use this constructor to specify a completed Task if the Task already has an id (copy of
     * another Task).
     *
     * @param title       title of the task
     * @param description description of the task
     * @param id          id of the task
     * @param completed   true if the task is completed, false if it's active
     */
    public Task(@Nullable String title, @Nullable String description, @NonNull int priority, @NonNull int color, @NonNull long dueAt, @NonNull boolean hasAlarm, @NonNull String id, boolean completed) {
        mId = id;
        mTitle = title;
        mDescription = description;
        this.priority = priority;
        this.color = color;
        this.dueAt = dueAt;
        this.hasAlarm = hasAlarm;
        isCompleted = completed;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    @NonNull
    public int getPriority() {
        return this.priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isActive() {
        return !isCompleted;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mTitle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equal(mId, task.mId) &&
                Objects.equal(mTitle, task.mTitle) &&
                Objects.equal(mDescription, task.mDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mTitle, mDescription);
    }

    @Override
    public String toString() {
        return "Task with title " + mTitle;
    }

    @NonNull
    public int getColor() {
        return color;
    }

    @NonNull
    public long getDueAt() {
        return dueAt;
    }

    @NonNull
    public boolean hasAlarm() {
        return hasAlarm;
    }

    public void setCompleted(boolean isCompleted){ this.isCompleted = isCompleted;}
}
