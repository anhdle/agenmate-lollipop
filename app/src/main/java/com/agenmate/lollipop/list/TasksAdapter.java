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

        public ViewHolder(View view) {
            super(view);
            titleText = (TextView)view.findViewById(R.id.title_text);
            descText = (TextView)view.findViewById(R.id.desc_text);
            priorityText = (TextView)view.findViewById(R.id.priority_text);
            balloon = (ImageView)view.findViewById(R.id.balloon);
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
        Task task = tasks.get(position);
        viewHolder.getTitleText().setText(tasks.get(position).getTitle());
        viewHolder.getDescText().setText((tasks.get(position).getDescription()));
        viewHolder.getPriorityText().setText((getPriorityText(tasks.get(position).getPriority())));
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

