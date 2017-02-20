package com.agenmate.lollipop.alarm;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Vibrator;

import com.agenmate.lollipop.addedit.AddEditActivity;

/**
 * Created by kincaid on 1/31/17.
 */

public class AlarmService extends Service {

    // Time period between two vibration events
    private final static int VIBRATE_DELAY_TIME = 2000;
    // Vibrate for 1000 milliseconds
    private final static int DURATION_OF_VIBRATION = 1000;
    // Increase alarm volume gradually every 600ms
    private final static int VOLUME_INCREASE_DELAY = 600;
    // Volume level increasing step
    private final static float VOLUME_INCREASE_STEP = 0.01f;
    // Max player volume level
    private final static float MAX_VOLUME = 1.0f;

    private MediaPlayer mediaPlayer;
    private Vibrator mVibrator;
    private float mVolumeLevel = 0;

    private Handler mHandler = new Handler();
    private Runnable mVibrationRunnable = new Runnable() {
        @Override
        public void run() {
            mVibrator.vibrate(DURATION_OF_VIBRATION);
            // Provide loop for vibration
            mHandler.postDelayed(mVibrationRunnable,
                    DURATION_OF_VIBRATION + VIBRATE_DELAY_TIME);
        }
    };

    private Runnable mVolumeRunnable = new Runnable() {
        @Override
        public void run() {
            // increase volume level until reach max value
            if (mediaPlayer != null && mVolumeLevel < MAX_VOLUME) {
                mVolumeLevel += VOLUME_INCREASE_STEP;
                mediaPlayer.setVolume(mVolumeLevel, mVolumeLevel);
                // set next increase in 600ms
                mHandler.postDelayed(mVolumeRunnable, VOLUME_INCREASE_DELAY);
            }
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = (mp, what, extra) -> {
        mp.stop();
        mp.release();
        mHandler.removeCallbacksAndMessages(null);
        AlarmService.this.stopSelf();
        return true;
    };





    private boolean isRunning;
    private Context context;
    private int startId;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        HandlerThread ht = new HandlerThread("alarm_service");
        ht.start();
        mHandler = new Handler(ht.getLooper());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startPlayer();
        // Start the activity where you can stop alarm
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setComponent(new ComponentName(this, AddEditActivity.class));
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);

     /*   final NotificationManager mNM = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(this.getApplicationContext(), AddEditActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        Notification mNotify  = new Notification.Builder(this)
                .setContentTitle("Title" + "!")
                .setContentText("Text!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        String state = intent.getExtras().getString("extra");

        assert state != null;
        switch (state) {
            case "no":
                startId = 0;
                break;
            case "yes":
                startId = 1;
                break;
            default:
                startId = 0;
                break;
        }

        // get richard's thing
        if(!this.isRunning && startId == 1) {
            Log.e("if there was not sound ", " and you want start");

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);

            Log.v("ringtonestart", String.valueOf(ringtone));
            ringtone.play();


            mNM.notify(0, mNotify);

            this.isRunning = true;
            this.startId = 0;

        }
        else if (!this.isRunning && startId == 0){
            Log.e("if there was not sound ", " and you want end");

            this.isRunning = false;
            this.startId = 0;

        }

        else if (this.isRunning && startId == 1){
            Log.e("if there is sound ", " and you want start");

            this.isRunning = true;
            this.startId = 0;

        }
        else {
            Log.e("if there is sound ", " and you want end");
            Log.v("ringtonestop", String.valueOf(ringtone));
            ringtone.stop();

            this.isRunning = false;
            this.startId = 0;
        }


        Log.e("MyActivity", "In the service");
        */

        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        //super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        this.isRunning = false;
    }


    private void startPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnErrorListener(mErrorListener);

        try {
            // add vibration to alarm alert if it is set
            if(true) {
           // if (App.getState().settings().vibrate()) {
                mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                mHandler.post(mVibrationRunnable);
            }
            // Player setup is here
           // String ringtone = App.getState().settings().ringtone();

            String ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                 //   && ringtone.startsWith("content://media/external/")
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
            }*/
            mediaPlayer.setDataSource(this, Uri.parse(ringtone));
            mediaPlayer.setLooping(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setVolume(mVolumeLevel, mVolumeLevel);
            mediaPlayer.prepare();
            mediaPlayer.start();

            if(true){
          //  if (App.getState().settings().ramping()) {
                mHandler.postDelayed(mVolumeRunnable, VOLUME_INCREASE_DELAY);
            } else {
                mediaPlayer.setVolume(MAX_VOLUME, MAX_VOLUME);
            }
        } catch (Exception e) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            stopSelf();
        }
    }

}