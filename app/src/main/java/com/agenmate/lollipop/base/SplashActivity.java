package com.agenmate.lollipop.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.agenmate.lollipop.R;
import com.agenmate.lollipop.list.ListActivity;
import com.agenmate.lollipop.util.FontUtils;
import com.squareup.seismic.ShakeDetector;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by kincaid on 12/17/16.
 */

public class SplashActivity extends BaseActivity implements ShakeDetector.Listener{

    private static final ButterKnife.Action<View> ALPHA_FADE = (view, index) -> {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(1500);
        alphaAnimation.setStartOffset(index * 300);
        alphaAnimation.setFillAfter(true);
        view.startAnimation(alphaAnimation);
    };

    @BindViews({ R.id.slogan, R.id.card_bottom_right, R.id.card_top_right, R.id.card_top_left, R.id.logo}) List<View> animViews;

    @BindView(R.id.logo) ImageView logo;
    @BindView(R.id.card_top_left) View card1;
    @BindView(R.id.card_top_right) View card2;
    @BindView(R.id.card_bottom_right) View card3;
    @BindView(R.id.slogan) TextView slogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        slogan.setTypeface(FontUtils.get(this, "Dudu"));

        /*SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);*/

        new Handler().postDelayed(() -> {
            ButterKnife.apply(animViews, ALPHA_FADE);
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(getBaseContext(), ListActivity.class);
                startActivity(intent);
                finish();
            }, 3000);
        }, 300);
    }

    @Override
    public void hearShake() {
        //Log.v("shake", "ah");
    }

    @Override
    protected void onPause() {
        super.onPause();
        exitToBottomAnimation();
    }
}
