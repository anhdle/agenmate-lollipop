package com.agenmate.lollipop.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;

/**
 * Created by kincaid on 2/9/17.
 */

public class AccessibilityUtils {

    @SuppressLint("NewApi")
    public static void tryAccessibilityAnnounce(View view, CharSequence text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && view != null && text != null) {
            view.announceForAccessibility(text);
        }
    }
}
