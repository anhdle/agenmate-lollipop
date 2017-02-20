package com.agenmate.lollipop.util;

import android.content.Context;

import com.agenmate.lollipop.R;

/**
 * Created by kincaid on 2/9/17.
 */

public class ThemeUtils {

    /**
     * Gets dialog type (Light/Dark) from current theme
     * @param context The context to use as reference for the boolean
     * @param current Default value to return if cannot resolve the attribute
     * @return true if dark mode, false if light.
     */
    public static boolean isDarkTheme(Context context, boolean current) {
        return AttrUtils.resolveBoolean(context, R.attr.mdtp_theme_dark, current);
    }
}
