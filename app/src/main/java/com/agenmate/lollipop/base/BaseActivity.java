package com.agenmate.lollipop.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.agenmate.lollipop.R;

/**
 * Created by kincaid on 12/17/16.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected int[] colorIds = {R.color.md_red_900, R.color.md_orange_900, R.color.md_yellow_900, R.color.md_green_900, R.color.md_blue_900, R.color.md_indigo_900, R.color.md_deep_purple_900};


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Perform injection so that when this call returns all dependencies will be available for use.
        //((AppController) getApplication()).getComponent().inject(this);

        setStatusBarColor(0);

    }

    protected void enterFromBottomAnimation(){
        overridePendingTransition(R.anim.activity_open_translate_from_bottom, R.anim.activity_no_animation);
    }

    protected void exitToBottomAnimation(){
        overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_close_translate_to_bottom);
    }
    
    public void setStatusBarColor(int color){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, colorIds[color]));
        }
    }
}
