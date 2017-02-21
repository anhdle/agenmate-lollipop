package com.agenmate.lollipop.ui.layout.timepicker;

import android.content.Context;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.agenmate.lollipop.R;
import com.agenmate.lollipop.ui.layout.ArcMenu;
import com.agenmate.lollipop.util.TimeUtils;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kincaid on 2/5/17.
 */

public class TimePickerLayout extends ConstraintLayout {

    public enum Mode { DATE, TIME };
    public String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN",
                            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    public String[] daysOfWeek = {"S", "M", "T", "W", "T", "F", "S"};
    private Mode mode;

    private int previousDayOfWeek = -1;
    private int year, month, dayOfMonth, hour, minute, dayOfWeek;
    private boolean isAM;
    private ImageButton[] colorButtons;
    private Animation fadeAnimation;
    public static final int NO_DUE_DATE = 0;
    public static final int HAS_DUE_DATE = 1;
    public static final int HAS_TIME = 2;

    private DateTime now;
    @BindView(R.id.container_hour) FrameLayout hourContainer;
    @BindView(R.id.container_minute) FrameLayout minuteContainer;
    @BindView(R.id.container_am_pm) FrameLayout AMPMContainer;
    @BindView(R.id.seekArcHour) SeekArc bigArc;
    @BindView(R.id.seekArcMinute) SeekArc smallArc;
    @BindView(R.id.seekArcAMPM) FlippingArc amPmArc;
    @BindView(R.id.seekArcProgressHour) TextView hourProgress;
    @BindView(R.id.seekArcProgressMinute) TextView minuteProgress;
    @BindView(R.id.seekArcProgressAMPMTop) TextView amProgress;
    @BindView(R.id.seekArcProgressAMPMBottom) TextView pmProgress;
    @BindView(R.id.date_picker_year_next) Button next;
    @BindView(R.id.date_picker_year_prev) Button prev;
    @BindView(R.id.arc_menu_time) ArcMenu dayArcMenu;



    public TimePickerLayout(Context context) {
        super(context);
        init();
    }

    private int dueDateStatus;

    public TimePickerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.time_picker_layout, this, true); // your layout with <merge> as the root tag
        ButterKnife.bind(this);
        dayButtons = new Button[7];

        bigArc.setMax(12);
        bigArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                setBigArcProgressText(progress);
                if(mode == Mode.DATE){
                    if(month != progress){
                        month = progress;
                        adjustDay();
                        listener.onTimeChange(getTimeFromPicker());
                    }
                } else {
                    if(hour != progress){
                        hour = progress;
                        //adjustDay();
                        listener.onTimeChange(getTimeFromPicker());
                    }
                }

            }
        });


        smallArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                setSmallArcProgressText(progress);
                if(mode == Mode.DATE){
                    if(dayOfMonth != progress){
                        dayOfMonth = progress;
                        adjustDaysOfWeek();
                        listener.onTimeChange(getTimeFromPicker());
                    }
                } else {
                    if(minute != progress){
                        minute = progress % 60;
                        //adjustDay();
                        listener.onTimeChange(getTimeFromPicker());
                    }
                }
            }
        });

        amPmArc.setOnSeekArcChangeListener(new FlippingArc.OnSeekArcChangeListener() {

            @Override
            public void onStartTrackingTouch(FlippingArc flippingArc) {

            }

            @Override
            public void onStopTrackingTouch(FlippingArc flippingArc) {
                if(amProgress.getAlpha() > pmProgress.getAlpha()){
                    isAM = true;
                    amProgress.setAlpha(1f);
                    pmProgress.setAlpha(.1f);
                    amPmArc.setProgress(0);
                    listener.onTimeChange(getTimeFromPicker());
                } else {
                    isAM = false;
                    amProgress.setAlpha(.1f);
                    pmProgress.setAlpha(1f);
                    amPmArc.setProgress(5);
                    listener.onTimeChange(getTimeFromPicker());
                }
            }

            @Override
            public void onProgressChanged(FlippingArc flippingArc, int progress, boolean fromUser) {
                amProgress.setAlpha(progress < 6 ? Math.max(1 - progress * .2f, .1f) : -1f + progress * .2f);
                pmProgress.setAlpha(progress < 6 ? Math.max(.2f * progress, .1f): 2f - progress * .2f);
            }
        });

        next.setOnClickListener(v -> {
            amProgress.setText(String.valueOf(++year));
            if(year > now.getYear() + 4) next.setVisibility(GONE);
            prev.setVisibility(VISIBLE);
            adjustDaysOfWeek();
            listener.onTimeChange(getTimeFromPicker());
        });

        prev.setOnClickListener(v -> {
            amProgress.setText(String.valueOf(--year));
            if(year == now.getYear()) prev.setVisibility(GONE);
            next.setVisibility(VISIBLE);
            adjustDaysOfWeek();
            listener.onTimeChange(getTimeFromPicker());
        });

        for (int i = 0; i < 7; i++) {
            final int index = i;
            Button button = new Button(getContext());
            button.setBackgroundResource(R.drawable.round_button_no_shadow);
            button.setText(daysOfWeek[i]);
            dayButtons[i] = button;
            dayArcMenu.addItem(button, null
                    // TODO add extra actions
                    /*v -> {
                v.setAlpha(1f);
                colorButtons[index].setAlpha(1f);
                if(index != previousDayOfWeek){

                    dayButtons[previousDayOfWeek].setAlpha(0.2f);
                    previousDayOfWeek = index;

                   // setTime.set(Calendar.DAY_OF_WEEK, previousDayOfWeek + 1);
                }
            }*/
            );
        }

        dayArcMenu.setOnArcAnimationEndListener(isExpanded -> {
            for(int i = 0; i < 7; i++) {
                int finalI = i;
                if(isExpanded){
                    if(finalI != previousDayOfWeek)
                        new Handler().postDelayed(() -> dayButtons[finalI].setAlpha(0.2f), 100 * finalI);
                } else dayButtons[finalI].setAlpha(1f);
            }


        });

        dayArcMenu.setControlLayoutClickListener(v -> {
            if(mode == Mode.DATE){
                if(dueDateStatus == HAS_DUE_DATE){
                    dueDateStatus = HAS_TIME;
                    resetTime();

                } else { // TODO if already has time
                    setUIMode(Mode.TIME);
                }
            } else {
                setUIMode(Mode.DATE);
            }
        });

        resetDate();

        /*fadeAnimation = new AlphaAnimation(1f, 0.2f);
        fadeAnimation.setDuration(100);
        fadeAnimation.setInterpolator(new AccelerateInterpolator());
        fadeAnimation.setFillAfter(true);*/
    }

    private Button [] dayButtons;


    public void setUIMode(Mode mode){
        this.mode = mode;
        if(mode == Mode.TIME){
            dayArcMenu.setControlLayoutDrawable(R.drawable.ic_calendar);
            bigArc.setProgress(hour);
            setBigArcProgressText(hour);

            smallArc.setMax(60);
            smallArc.setProgress(minute);
            setSmallArcProgressText(minute);

            amPmArc.setEnabled(true);
            amPmArc.setProgress(isAM ? 0 : 5);
            amPmArc.invalidate();
            amProgress.setText("AM");
            pmProgress.setVisibility(VISIBLE);

            next.setVisibility(GONE);
            prev.setVisibility(GONE);
        } else {
            dayArcMenu.setControlLayoutDrawable(R.drawable.ic_clock);
            bigArc.setProgress(month);
            setBigArcProgressText(month);

            smallArc.setMax(TimeUtils.getMaxDayOfMonth(year, month));
            smallArc.setProgress(dayOfMonth);
            setSmallArcProgressText(dayOfMonth);

            amPmArc.setEnabled(false);
            amPmArc.invalidate();
            amProgress.setText(String.valueOf(year));
            amProgress.setAlpha(1f);
            pmProgress.setVisibility(GONE);

            if(year < now.getYear() + 4) next.setVisibility(VISIBLE);
            if(year > now.getYear()) prev.setVisibility(VISIBLE);
            adjustDaysOfWeek();
        }
    }

    public Mode getMode() {
        return mode;
    }

    public void setBigArcProgressText(int progress){
        if(mode == Mode.TIME){
            hourProgress.setText(String.valueOf(progress));
        } else {
            hourProgress.setText(months[progress - 1]);
        }
    }

    public void setSmallArcProgressText(int progress){
        if(mode == Mode.TIME){
            int p = progress % 60;
            minuteProgress.setText(String.valueOf(p < 10 ? "0" + p : p));
        } else {
            minuteProgress.setText(String.valueOf(progress));
        }

    }

    public void setColorArcMenu(ImageButton[] colorButtons){
        this.colorButtons = colorButtons;
    }

    public ArcMenu getDayArcMenu(){
        return dayArcMenu;
    }

    public void adjustDaysOfWeek(){
        Log.v("prevdowin", String.valueOf(previousDayOfWeek));
        dayOfWeek = TimeUtils.getDayOfWeek(year, month, dayOfMonth);
        Log.v("dowin", String.valueOf(dayOfWeek));
        if(previousDayOfWeek != dayOfWeek){
            dayButtons[previousDayOfWeek].setAlpha(0.2f);
            dayButtons[dayOfWeek].setAlpha(1f);
            previousDayOfWeek = dayOfWeek;
        }
    }

    public void adjustDay(){
        int maxDayInMonth = TimeUtils.getMaxDayOfMonth(year, month);
        smallArc.setMax(maxDayInMonth);
        smallArc.setProgress(Math.min(smallArc.getProgress(), maxDayInMonth));
        adjustDaysOfWeek();
    }

    public DateTime getTimeFromPicker(){
        // TODO Fix Illegal instant due to time zone offset transition (daylight savings time 'gap'): 2017-03-12T02:53:00.000 (America/New_York)
        return new DateTime(year, month, dayOfMonth, convert12To24(), minute, 0, 000);
    }

    public interface OnTimePickerChangeListener{
        void onTimeChange(DateTime time);
    }

    private OnTimePickerChangeListener listener;

    public void setOnTimePickerChangeListener(OnTimePickerChangeListener listener){
        this.listener = listener;
        listener.onTimeChange(getTimeFromPicker());
    }

    public void resetDate(){
        now = new DateTime();
        year = now.getYear();
        month = now.getMonthOfYear();
        dayOfMonth = now.getDayOfMonth();
        dayOfWeek = now.getDayOfWeek() % 7;
        previousDayOfWeek = dayOfWeek;

        setUIMode(Mode.DATE);
    }

    public void resetTime(){
        now = new DateTime();
        hour = now.getHourOfDay();
        isAM = hour < 12;
        hour = isAM ? hour : hour - 12;
        hour = hour == 0 ? 12 : hour;
        minute = now.getMinuteOfHour(); // TODO allow time + 1

        setUIMode(Mode.TIME);
        listener.onTimeChange(getTimeFromPicker());
    }

    private int convert12To24(){
        int hourIn24;
        hourIn24 = hour == 12 ? 0 : hour;
        hourIn24 = isAM ? hourIn24 : hourIn24 + 12;
        return hourIn24;
    }

    public int getDueDateStatus(){
        return dueDateStatus;
    }

    public void setDueDateStatus(int status){
        this.dueDateStatus = status;
    }

    public void toggleAlarm(){
        dueDateStatus = dueDateStatus == 1 ? 2 : 1;
    }

}
