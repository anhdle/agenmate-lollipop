package com.agenmate.lollipop.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by kincaid on 2/9/17.
 */

public class ScreenUtils {

    public static int dpToPx(Context context, int dp){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        //float dpHeight = dm.heightPixels / dm.density;
        //float dpWidth = dm.widthPixels / dm.density;;
        return (int)((dp * dm.density) + 0.5);
    }
}
