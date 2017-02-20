package com.agenmate.lollipop.util;

import android.text.Html;
import android.text.Spanned;

/**
 * Created by kincaid on 2/19/17.
 */

public class MarkupUtils {
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html. FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
