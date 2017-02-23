package com.agenmate.lollipop.list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.agenmate.lollipop.R;
import com.agenmate.lollipop.data.Task;
import com.agenmate.lollipop.util.FontUtils;
import com.agenmate.lollipop.util.MarkupUtils;
import com.agenmate.lollipop.util.ScreenUtils;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kincaid on 1/9/17.
 */

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<Task> tasks;
    private TaskItemListener itemListener;
    private Context context;
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
        private final CheckBox active;
        private final FrameLayout balloonContainer;
        public boolean isPlayed;

        public ViewHolder(View view) {
            super(view);
            titleText = (TextView)view.findViewById(R.id.title_text);
            descText = (TextView)view.findViewById(R.id.desc_text);
            priorityText = (TextView)view.findViewById(R.id.priority_text);
            balloon = (ImageView)view.findViewById(R.id.balloon);
            alarm = (ImageView)view.findViewById(R.id.alarm_button);
            trash = (ImageView)view.findViewById(R.id.delete_button);
            active = (CheckBox)view.findViewById(R.id.active_button);
            balloonContainer = (FrameLayout)view.findViewById(R.id.balloon_container);

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

        public FrameLayout getBalloonContainer() {
            return balloonContainer;
        }

        public CheckBox getActive() {
            return active;
        }
    }

    public TasksAdapter(Context context, List<Task> tasks, TaskItemListener itemListener) {
        setList(tasks);
        this.context = context;
        this.itemListener = itemListener;
    }

    public void replaceData(List<Task> tasks, boolean notify) {
        setList(tasks);
        if(notify) notifyDataSetChanged();
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
        final int priority = task.getPriority();
        formatText(viewHolder.getTitleText(), "<b>"+ task.getTitle() + "</b>", Color.BLACK, 0);
        formatText(viewHolder.getDescText(), task.getDescription(), Color.BLACK, 0);
        formatText(viewHolder.getPriorityText(), getPriorityText(priority), Color.WHITE, priority);
        final ImageView balloon = viewHolder.getBalloon();
        int size = ScreenUtils.dpToPx(context, priority * 20 + 40);
        balloon.setImageResource(balloonIds[task.getColor()]);
        balloon.setLayoutParams(new FrameLayout.LayoutParams(size, size, Gravity.CENTER));
        balloon.setOnClickListener(v -> itemListener.onTaskClick(task));


        final ImageView alarm = viewHolder.getAlarm();
        alarm.setVisibility(task.hasAlarm() ? View.VISIBLE : View.GONE);
        alarm.setOnClickListener(v -> {
            // TODO toggle alarm
        });
        viewHolder.getTrash().setOnClickListener(v -> itemListener.onTaskDelete(task));

        final CheckBox active = viewHolder.getActive();
        active.setChecked(task.isCompleted());
        active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //task.setCompleted(true);
                    //tasks.set(position, task);
                    itemListener.onCompleteTaskClick(task);
                } else {
                    //task.setCompleted(false);
                    //tasks.set(position, task);
                    itemListener.onActivateTaskClick(task);
                }
            }
        });

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.isPlayed = false;
        //Log.v("detach", String.valueOf(holder.isPlayed));
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if(!holder.isPlayed){
            startAnimation(holder.getBalloon());
            holder.isPlayed = true;
        }
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

    private int[] balloonIds = { R.drawable.animated_balloon_red, R.drawable.animated_balloon_orange, R.drawable.animated_balloon_yellow, R.drawable.animated_balloon_green,
                                R.drawable.animated_balloon_blue, R.drawable.animated_balloon_indigo, R.drawable.animated_balloon_purple} ;

    private  void startAnimation(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    private void formatText(TextView textView, String string, int color, int size){
        if(string != null){
            textView.setText(MarkupUtils.fromHtml(string));
        }
        textView.setTextColor(color);
        textView.setTypeface(FontUtils.get(context, "Dudu"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20 + size * 5);
    }
}

