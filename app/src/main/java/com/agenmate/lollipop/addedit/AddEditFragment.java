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

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.agenmate.lollipop.R;
import com.agenmate.lollipop.ui.SnappingSeekBar;
import com.agenmate.lollipop.ui.layout.ArcMenu;
import com.agenmate.lollipop.ui.layout.timepicker.TimePickerLayout;
import com.agenmate.lollipop.util.FontUtils;
import com.agenmate.lollipop.util.MarkupUtils;
import com.agenmate.lollipop.util.ScreenUtils;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
public class AddEditFragment extends Fragment implements AddEditContract.View {

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    private AddEditContract.Presenter presenter;
    private ImageButton [] colorButtons;
    boolean showFAB = true;

    private ArcMenu timeMenu;
    private static int timeHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    private static int timeMinute = Calendar.getInstance().get(Calendar.MINUTE);
    private int[] colorTabIds = {R.color.md_red_700, R.color.md_orange_700, R.color.md_yellow_700, R.color.md_green_700, R.color.md_blue_700, R.color.md_indigo_700, R.color.md_deep_purple_700};
    private int[] colorBackgroundIds = {R.color.md_red_50, R.color.md_orange_50, R.color.md_yellow_50, R.color.md_green_50, R.color.md_blue_50, R.color.md_indigo_50, R.color.md_deep_purple_50};
    private int[] colorDrawables = {R.drawable.red_round_button, R.drawable.orange_round_button, R.drawable.yellow_round_button, R.drawable.green_round_button, R.drawable.blue_round_button, R.drawable.indigo_round_button, R.drawable.violet_round_button} ;
    private int selectedColor;
    private Unbinder unbinder;
    private DateTime setTime;
    private int timeFormat;

    @BindViews({ R.id.title_text_view, R.id.title_card_view, R.id.desc_text_view, R.id.desc_card_view, R.id.priority_text_view, R.id.priority_card_view, R.id.color_text_view}) List<View> bottomViews;

    @BindView(R.id.arc_menu_color) ArcMenu arcMenu;
    @BindView(R.id.time_bottom_sheet) NestedScrollView sheet;
    @BindView(R.id.time_picker_layout) TimePickerLayout timePickerLayout;
    @BindView(R.id.due_date_text_view) TextView dueDateText;
    @BindView(R.id.due_date_status) TextView dueDateStatus;
    @BindView(R.id.due_date_teller) TextView dueDateTeller;
    @BindView(R.id.task_background) CoordinatorLayout background;
    @BindView(R.id.title_text_view) TextView titleText;
    @BindView(R.id.title_edit_text) EditText titleEdit;
    @BindView(R.id.desc_text_view) TextView descText;
    @BindView(R.id.desc_edit_text) EditText descEdit;
    @BindView(R.id.color_text_view) TextView colorText;
    @BindView(R.id.priority_text_view) TextView priorityText;
    @BindView(R.id.priority_seek_bar) SnappingSeekBar seekBar;

    private static final ButterKnife.Action<View> ALPHA_APPEAR = (view, index) -> {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(250);
        alphaAnimation.setStartOffset(index * 250);
        alphaAnimation.setFillAfter(true);
        view.startAnimation(alphaAnimation);
    };

    private final ButterKnife.Action<View> ALPHA_FADE = (view, index) -> {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(250);
        alphaAnimation.setStartOffset(index * 250);
        alphaAnimation.setFillAfter(true);
        if(index == 5){
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(timePickerLayout.getDueDateStatus() == TimePickerLayout.NO_DUE_DATE){
                        timePickerLayout.setDueDateStatus(TimePickerLayout.HAS_DUE_DATE);
                        setDueDateText(dueDateTeller);
                        ButterKnife.apply(dueDateTeller, ALPHA_APPEAR);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        view.startAnimation(alphaAnimation);
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        colorButtons = new ImageButton[7];
    }

    public static AddEditFragment newInstance() {
        return new AddEditFragment();
    }

    public AddEditFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    public void setPresenter(@NonNull AddEditContract.Presenter presenter) {
        this.presenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        timePickerLayout.setColorArcMenu(colorButtons);

        formatText(titleText, "<b>T</b>itle");
        formatText(titleEdit, null);
        formatText(descText, "<b>D</b>escription");
        formatText(descEdit, null);
        formatText(colorText, "<b>C</b>olor <b>T</b>ag");
        formatText(priorityText, "<b>P</b>riority");
        formatText(dueDateText, "<b>D</b>ue <b>D</b>ate");
        formatText(dueDateTeller, null);
        formatText(dueDateStatus, "(Scroll up to set)");

        seekBar.setProgressDrawable(R.drawable.progress_bar);
        seekBar.setItems(new String[]{"Low", "Medium", "High"});
        seekBar.setTextIndicatorColor(Color.BLACK);
        seekBar.setIndicatorColor(ContextCompat.getColor(getActivity(), R.color.md_grey_300));
        seekBar.setTextSize(14);
        seekBar.setIndicatorSize(4);

        BottomSheetBehavior.from(sheet)
        .setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        dueDateStatus.setText("");
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        if(previousState == BottomSheetBehavior.STATE_EXPANDED){
                            previousState = newState;
                            ButterKnife.apply(bottomViews, ALPHA_APPEAR);
                            onColorArcExpanded();
                            setDueDateText(dueDateStatus);
                            if(timeMenu.isExpanded())timeMenu.switchState();
                        }

                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        if(previousState == BottomSheetBehavior.STATE_COLLAPSED){
                            previousState = newState;
                            ButterKnife.apply(bottomViews, ALPHA_FADE);
                            for(int i = 0; i < 7; i++)
                                colorButtons[i].setAlpha(1f);
                            if(!timeMenu.isExpanded()) timePickerLayout.getDayArcMenu().switchState();
                        }

                        break;
                }

            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {}
        });

        /*ViewUtils.waitForLayoutPrepared(seekBar, new ViewUtils.LayoutPreparedListener() {
            @Override
            public void onLayoutPrepared(final View preparedView) {
                seekBar.setProgressToIndex(1);
            }
        });*/


      /*
              Button start_alarm= (Button) root.findViewById(R.id.start_alarm);
        start_alarm.setOnClickListener(v -> {
            setAlarmText("Alarm set");
            int hour;
            int minute;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                hour = alarmTimePicker.getCurrentHour();
                minute = alarmTimePicker.getCurrentMinute();

            } else {
                hour = alarmTimePicker.getHour();
                minute = alarmTimePicker.getMinute();
            }
            presenter.getAlarmController().resetAlarm(minute, hour);
        });

        Button dismiss = (Button)root.findViewById(R.id.dismiss_alarm);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarmText("Alarm dismiss");
                presenter.getAlarmController().dismissAlarm();
            }
        });

        Button cancel= (Button) root.findViewById(R.id.cancel_alarm);
        cancel.setOnClickListener(v -> {
            setAlarmText("Alarm cancel");
            presenter.getAlarmController().cancelAlarm();
        });*/


        for (int i = 0; i < 7; i++) {
            final int index = i;
            final ImageButton button = new ImageButton(getActivity());
            button.setBackgroundResource(colorDrawables[i]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.setElevation(ScreenUtils.dpToPx(getActivity(), 2));
            }
            colorButtons[i] = button;

            arcMenu.addItem(button, v -> {
                v.setAlpha(0.2f);
                if(index != selectedColor){
                    colorButtons[selectedColor].setAlpha(1f);
                    setColor(index);
                }
            });
        }

        timeMenu = timePickerLayout.getDayArcMenu();

        arcMenu.setOnArcAnimationEndListener(isExpanded -> {
            if(isExpanded) onColorArcExpanded();
        });
        arcMenu.switchState();

        return rootView;
    }

    @Override
    public void showEmptyTaskError() {
        Snackbar.make(titleText, getString(R.string.empty_task_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTasksList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void showMissingTask() {
        titleEdit.setText("");
    }

    @Override
    public void setTitle(String title) {
        titleEdit.setText(title);
    }

    @Override
    public void setDescription(String description) {
        descEdit.setText(description);
    }

    @Override
    public void setPriority(int priority) {
        seekBar.setProgress(priority * 50);

    }

    @Override
    public void setColor(int color) {
        selectedColor = color;
        boolean isWhiteText = selectedColor == 0 || selectedColor == 5 || selectedColor == 6;
        if(isWhiteText){
            dueDateStatus.setTextColor(Color.WHITE);
            dueDateText.setTextColor(Color.WHITE);
        } else {
            dueDateStatus.setTextColor(Color.BLACK);
            dueDateText.setTextColor(Color.BLACK);
        }
        background.setBackgroundResource(colorBackgroundIds[selectedColor]);
        dueDateText.setBackgroundResource(colorTabIds[selectedColor]);
        ((AddEditActivity)getActivity()).setBarColor(selectedColor, isWhiteText);
    }

    @Override
    public void setDueDate(long dueDate) {
        setTime = timePickerLayout.resetDate(dueDate);
        setDueDateText(dueDateStatus);


        timePickerLayout.setOnTimePickerChangeListener(time -> {
            // TODO check if time need to mark change later
            setTime = time;
            setDueDateText(dueDateTeller);
        });
    }

    private void setDueDateText(TextView textView){
        int status = timePickerLayout.getDueDateStatus();
        if(status != TimePickerLayout.NO_DUE_DATE){
            timeFormat =  timePickerLayout.isAtMidnight() ?
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY :
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_TIME;
            textView.setText(DateUtils.formatDateTime(getActivity(), setTime, timeFormat));
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showTaskDeleted() {
        getActivity().finish();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onColorArcExpanded(){
        for(int i = 0; i < 7; i++){
            colorButtons[i].setAlpha(1f);
            colorButtons[selectedColor].setAlpha(0.2f);
        }
    }

    private void formatText(TextView textView, String string){
        if(string != null){
            textView.setText(MarkupUtils.fromHtml(string));
        }
        textView.setTextColor(Color.BLACK);
        textView.setTypeface(FontUtils.get(getActivity(), "Dudu"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
    }



    public void onOptionsCreated(){
        ((AddEditActivity)getActivity()).setBarColor(selectedColor, selectedColor == 0 || selectedColor == 5 || selectedColor == 6);
    }

    private int previousState = BottomSheetBehavior.STATE_COLLAPSED;

    private boolean hasAlarm;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_save:
                String title = emptyStringChecker(titleEdit.getText().toString());
                if(title == null) {
                    Snackbar.make(background, "Title can't be Empty", Snackbar.LENGTH_LONG).show();
                    return true;
                }
                String description = emptyStringChecker(descEdit.getText().toString());
                int priority = getPriority();

                long dueAt = getDueDate();
                if(dueAt < 0) {
                    Snackbar.make(background, "Not enough time to complete task at due date", Snackbar.LENGTH_LONG).show();
                    return true;
                }

                presenter.saveTask(title, description, priority, selectedColor, dueAt, hasAlarm);

                return true;
            case R.id.action_alarm:
                if(!hasAlarm){
                    if(getDueDate() < 0){
                        Snackbar.make(background, "Not enough time to complete task at due date", Snackbar.LENGTH_LONG).show();
                        return true;
                    }
                }
                boolean setWhite = selectedColor == 0 || selectedColor == 5 || selectedColor == 6;
                item.setIcon(hasAlarm ? R.drawable.ic_alarm_off : R.drawable.ic_alarm_on);
                Drawable drawable = item.getIcon();
                drawable.mutate();
                drawable.setColorFilter(setWhite ? Color.WHITE : Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                hasAlarm = !hasAlarm;
                return true;

            case R.id.action_delete:
                presenter.deleteTask();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String emptyStringChecker(String string){
        if (string != null) {
            string = string.trim();
            if (!string.isEmpty() ) {
                return string;
            }
        }
        return null;
    }

    private int getPriority(){
        return seekBar.getProgress() / 50;
    }

    private long getDueDate(){
        if(timePickerLayout.getDueDateStatus() == TimePickerLayout.NO_DUE_DATE) return 0;
        else {
            long time = timePickerLayout.getTimeFromPicker().getMillis() * 1000;
            DateTime now = new DateTime().withSecondOfMinute(0).withMillisOfSecond(0).plusMinutes(1);
            if(now.getMillis() * 1000 < time) return time;
            else{
                // TODO turn off alarm
                return -1;
            }
        }
    }
}
