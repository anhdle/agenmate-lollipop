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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.agenmate.lollipop.R;
import com.agenmate.lollipop.ui.SnappingSeekBar;
import com.agenmate.lollipop.ui.layout.ArcMenu;
import com.agenmate.lollipop.ui.layout.timepicker.TimePickerLayout;
import com.agenmate.lollipop.util.FontUtils;
import com.agenmate.lollipop.util.MarkupUtils;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

import java.util.Calendar;

import butterknife.BindView;
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
    private int selectedColor = -1;
    private Unbinder unbinder;

    @BindView(R.id.arc_menu_color) ArcMenu arcMenu;
    @BindView(R.id.time_bottom_sheet) NestedScrollView sheet;
    @BindView(R.id.time_picker_layout) TimePickerLayout timePickerLayout;
    @BindView(R.id.alarm_teller) TextView alarmTeller;
    @BindView(R.id.task_background) CoordinatorLayout background;
    @BindView(R.id.title_text_view) TextView titleText;
    @BindView(R.id.title_edit_text) EditText titleEdit;
    @BindView(R.id.desc_text_view) TextView descText;
    @BindView(R.id.desc_edit_text) EditText descEdit;
    @BindView(R.id.color_text_view) TextView colorText;
    @BindView(R.id.priority_text_view) TextView priorityText;
    @BindView(R.id.priority_seek_bar) SnappingSeekBar seekBar;

    public static AddEditFragment newInstance() {
        return new AddEditFragment();
    }

    public AddEditFragment() {
        // Required empty public constructor
    }

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

        /*FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task_done);
       // fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener((View v) -> presenter.saveTask(mTitle.getText().toString(), mDescription.getText().toString()));*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        colorButtons = new ImageButton[7];
        timePickerLayout.setColorArcMenu(colorButtons);
        timePickerLayout.setOnTimePickerChangeListener(new TimePickerLayout.OnTimePickerChangeListener() {
            @Override
            public void onTimeChange(DateTime time) {
                alarmTeller.setText("DATE: " + DateUtils.formatDateTime(getActivity(), time, DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_WEEKDAY));
            }
        });

        // TODO get color from db
        selectedColor = 0;

        background.setBackgroundResource(colorBackgroundIds[selectedColor]);
        alarmTeller.setBackgroundResource(colorTabIds[selectedColor]);
        ((AddEditActivity)getActivity()).setTabColor(selectedColor);
        formatText(titleText, "<b>T</b>itle");
        formatText(titleEdit, null);
        formatText(descText, "<b>D</b>escription");
        formatText(descEdit, null);
        formatText(colorText, "<b>C</b>olor <b>T</b>ag");
        formatText(priorityText, "<b>P</b>riority");

        seekBar.setProgressDrawable(R.drawable.progress_bar);
        seekBar.setThumbDrawable(R.drawable.balloon_thumb);
        seekBar.setItems(new String[]{"Low", "Medium", "High"});
        seekBar.setProgressColor(Color.BLUE);
        //seekBar.setThumbnailColor(resources.getColor(R.color.yellow_light));
        seekBar.setTextIndicatorColor(Color.BLACK);
        seekBar.setIndicatorColor(ContextCompat.getColor(getActivity(), R.color.md_blue_grey_500));
        seekBar.setTextSize(14);
        seekBar.setIndicatorSize(14);
        /*ViewUtils.waitForLayoutPrepared(seekBar, new ViewUtils.LayoutPreparedListener() {
            @Override
            public void onLayoutPrepared(final View preparedView) {
                seekBar.setProgressToIndex(1);
            }
        });*/


        //setHasOptionsMenu(true);
        //setRetainInstance(true);


      /*

        alarmTextView = (TextView) root.findViewById(R.id.alarmText);
        // set the alarm to the time that you picked
        //calendar.add(Calendar.SECOND, 3);
        alarmTimePicker = (TimePicker) root.findViewById(R.id.alarmTimePicker);
        clockView = (ClockView)root.findViewById(R.id.clock_view);

        clockView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.v("progresschange", String.valueOf(progress));
                seekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.v("progressstart", String.valueOf(seekBar.getProgress()));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.v("progressend", String.valueOf(seekBar.getProgress()));

            }
        });



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
            colorButtons[i] = button;

            arcMenu.addItem(button, v -> {
                v.setAlpha(0.2f);
                if(index != selectedColor){
                    colorButtons[selectedColor].setAlpha(1f);
                    selectedColor = index;
                    background.setBackgroundResource(colorBackgroundIds[index]);
                    alarmTeller.setBackgroundResource(colorTabIds[index]);
                    ((AddEditActivity)getActivity()).setTabColor(selectedColor);
                }
            });
        }




        final Animation growAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.simple_grow);
        final Animation shrinkAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.simple_shrink);
        timeMenu = timePickerLayout.getDayArcMenu();

        BottomSheetBehavior
                .from(sheet)
                .setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.v("drag", "ae");
                        //if (showFAB) fab.startAnimation(shrinkAnimation);
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        onColorArcExpanded();

                        if(timeMenu.isExpanded())timeMenu.switchState();
                        Log.v("collapse", "c");

                      /*  showFAB = true;
                        fab.setVisibility(View.VISIBLE);
                        fab.startAnimation(growAnimation);*/
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        for(int i = 0; i < 7; i++)
                            colorButtons[i].setAlpha(1f);
                        if(!timeMenu.isExpanded()) timePickerLayout.getDayArcMenu().switchState();
                      //  showFAB = false;
                        break;


                }

            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });

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
    public void setTitle(String title) {
        titleText.setText(title);
    }

    @Override
    public void setDescription(String description) {
        descText.setText(description);
    }

    @Override
    public boolean isActive() {
        return isAdded();
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

        //if(timeMenu.isExpanded())timeMenu.switchState();
    }

    private void formatText(TextView textView, String string){
        if(string != null){
            textView.setText(MarkupUtils.fromHtml(string));
        }
        textView.setTextColor(Color.BLACK);
        textView.setTypeface(FontUtils.get(getActivity(), "Dudu"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
    }
}