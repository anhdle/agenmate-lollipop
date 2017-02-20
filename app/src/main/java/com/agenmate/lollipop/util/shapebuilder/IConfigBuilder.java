package com.agenmate.lollipop.util.shapebuilder;

import android.graphics.Shader;
import android.graphics.Typeface;

/**
 * Created by kincaid on 5/16/16.
 */
public interface IConfigBuilder {
    IConfigBuilder width(int width);

    IConfigBuilder height(int height);

    IConfigBuilder textColor(int color);

    IConfigBuilder withBorder(int thickness);

    IConfigBuilder borderColor(int color);

    IConfigBuilder gradientShader(Shader shader);

    IConfigBuilder useFont(Typeface font);

    IConfigBuilder fontSize(int size);

    IConfigBuilder bold();

    IConfigBuilder toUpperCase();

    ShapeDrawableBuilder.IShapeBuilder endConfig();


}