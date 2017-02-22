package com.agenmate.lollipop.list;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agenmate.lollipop.R;
import com.agenmate.lollipop.data.Task;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kincaid on 1/9/17.
 */

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<Task> tasks;
    private TaskItemListener itemListener;
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView descText;
        private final TextView priorityText;
        private final ImageView balloon;
        private final ImageView alarm;
        private final ImageView trash;

        public ViewHolder(View view) {
            super(view);
            titleText = (TextView)view.findViewById(R.id.title_text);
            descText = (TextView)view.findViewById(R.id.desc_text);
            priorityText = (TextView)view.findViewById(R.id.priority_text);
            balloon = (ImageView)view.findViewById(R.id.balloon);
            alarm = (ImageView)view.findViewById(R.id.alarm_button);
            trash = (ImageView)view.findViewById(R.id.delete_button);

        }

        public TextView getTitleText() {
            return titleText;
        }

        public TextView getDescText() {
            return descText;
        }

        public TextView getPriorityText() {
            return priorityText;
        }

        public ImageView getBalloon() {
            return balloon;
        }

        public ImageView getAlarm() { return alarm;}

        public ImageView getTrash() {return trash; }
    }

    public TasksAdapter(List<Task> tasks, TaskItemListener itemListener) {
        setList(tasks);
        this.itemListener = itemListener;
    }

    public void replaceData(List<Task> tasks) {
        setList(tasks);
        notifyDataSetChanged();
    }

    private void setList(List<Task> tasks) {
        this.tasks = checkNotNull(tasks);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final Task task = tasks.get(position);
        viewHolder.getTitleText().setText(task.getTitle());
        viewHolder.getDescText().setText((task.getDescription()));
        viewHolder.getPriorityText().setText((getPriorityText(task.getPriority())));
        viewHolder.getBalloon().setOnClickListener(v -> {
            itemListener.onTaskClick(task);
        });
        final ImageView alarm = viewHolder.getAlarm();
        alarm.setVisibility(task.hasAlarm() ? View.VISIBLE : View.GONE);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO toggle alarm
            }
        });
        viewHolder.getTrash().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.onTaskDelete(task);
            }
        });
        //viewHolder.getBalloon().setImageResource();

        // to finish
         /*
        if (!task.isCompleted()) {
            itemListener.onCompleteTaskClick(task);
        } else {
            itemListener.onActivateTaskClick(task);
        }
        */
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void clear() {
        tasks.clear();
        notifyDataSetChanged();
    }

    private String getPriorityText(int priority){
        switch (priority){
            case 1:
                return "M";
            case 2:
                return "H";
            default:
                return "L";
        }
    }

    private Drawable getBalloonDrawable(){
        return null;
    }
}

