package com.agenmate.lollipop.alarm;

/**
 * Created by kincaid on 2/2/17.
 */

public class AlarmSettings {

    private int ringtone;
    private boolean vibrate, ramping, snap;

    public int getRingtone() {
        return ringtone;
    }

    public void setRingtone(int ringtone) {
        this.ringtone = ringtone;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public boolean isRamping() {
        return ramping;
    }

    public void setRamping(boolean ramping) {
        this.ramping = ramping;
    }

    public boolean isSnap() {
        return snap;
    }

    public void setSnap(boolean snap) {
        this.snap = snap;
    }
}
