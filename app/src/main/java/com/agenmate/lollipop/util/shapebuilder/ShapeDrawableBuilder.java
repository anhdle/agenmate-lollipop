package com.agenmate.lollipop.util.shapebuilder;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;

public class ShapeDrawableBuilder extends ShapeDrawable {

    private final Paint textPaint;
    private final Paint borderPaint;
    private static final float SHADE_FACTOR = 0.9f;
    private final String text;
    private final int color;
    private final RectShape shape;
    private final int height;
    private final int width;
    private final int fontSize;
    private final float radius;
    private final int borderThickness;
    private final int borderColor;

    private ShapeDrawableBuilder(Builder builder) {
        super(builder.shape);

        // shape properties
        shape = builder.shape;
        height = builder.height;
        width = builder.width;
        radius = builder.radius;

        // text and color
        if(builder.text != null){
            text = builder.toUpperCase ? builder.text.toUpperCase() : builder.text;
            // text paint settings
            fontSize = builder.fontSize;
            textPaint = new Paint();
            textPaint.setColor(builder.textColor);
            textPaint.setAntiAlias(true);
            textPaint.setFakeBoldText(builder.isBold);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setTypeface(builder.font);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setStrokeWidth(builder.borderThickness);
        } else {
            fontSize = 0;
            text = null;
            textPaint = null;
        }

        color = builder.color;

        // border paint settings
        borderThickness = builder.borderThickness;
        borderColor = builder.borderColor;
        borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderThickness);
        borderPaint.setAntiAlias(true);

        // drawable paint color
        Paint paint = getPaint();
        if(!builder.isGradient) paint.setColor(color);
        else paint.setShader(builder.shader);
        paint.setAntiAlias(true);
    }

    private int getDarkerShade(int color) {
        return Color.rgb((int)(SHADE_FACTOR * Color.red(color)),
                (int)(SHADE_FACTOR * Color.green(color)),
                (int)(SHADE_FACTOR * Color.blue(color)));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect r = getBounds();

        // draw border
        if (borderThickness > 0) {
            drawBorder(canvas);
        }

        int count = canvas.save();
        canvas.translate(r.left, r.top);

        // draw text
        int width = this.width < 0 ? r.width() : this.width;
        int height = this.height < 0 ? r.height() : this.height;
        if(text!= null) {
            int fontSize = this.fontSize < 0 ? (Math.min(width, height) / 2) : this.fontSize;
            textPaint.setTextSize(fontSize);
            canvas.drawText(text, width / 2, height / 2 - ((textPaint.descent() + textPaint.ascent())), textPaint);
        }

        canvas.restoreToCount(count);

    }

    private void drawBorder(Canvas canvas) {
        RectF rect = new RectF(getBounds());
        rect.inset(borderThickness/2, borderThickness/2);

        if (shape instanceof OvalShape) {
            canvas.drawOval(rect, borderPaint);
        }
        else if (shape instanceof RoundRectShape) {
            canvas.drawRoundRect(rect, radius, radius, borderPaint);
        }
        else {
            canvas.drawRect(rect, borderPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        textPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        textPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }

    public static IShapeBuilder builder() {
        return new Builder();
    }

    public static class Builder implements IConfigBuilder, IShapeBuilder, IBuilder {

        private String text;
        private int color;
        private int borderThickness;
        private Integer borderColor;
        private int width;
        private int height;
        private Typeface font;
        private RectShape shape;
        public int textColor;
        private int fontSize;
        private boolean isBold;
        private boolean isGradient;
        private boolean toUpperCase;
        private Shader shader;
        public float radius;


        private Builder() {
            text = "";
            color = Color.GRAY;
            textColor = Color.WHITE;
            borderThickness = 0;
            borderColor = Color.WHITE;
            width = -1;
            height = -1;
            shape = new RectShape();
            font = Typeface.create("sans-serif-light", Typeface.NORMAL);
            fontSize = -1;
            isBold = false;
            toUpperCase = false;
        }

        public IConfigBuilder width(int width) {
            this.width = width;
            return this;
        }

        public IConfigBuilder height(int height) {
            this.height = height;
            return this;
        }

        public IConfigBuilder textColor(int color) {
            this.textColor = color;
            return this;
        }

        public IConfigBuilder withBorder(int thickness) {
            this.borderThickness = thickness;
            return this;
        }

        public IConfigBuilder gradientShader(Shader shader) {
            this.isGradient = true;
            this.shader = shader;
            return this;
        }


        public IConfigBuilder borderColor(int color) {
            this.borderColor = color;
            return this;
        }

        public IConfigBuilder useFont(Typeface font) {
            this.font = font;
            return this;
        }

        public IConfigBuilder fontSize(int size) {
            this.fontSize = size;
            return this;
        }

        public IConfigBuilder bold() {
            this.isBold = true;
            return this;
        }

        public IConfigBuilder toUpperCase() {
            this.toUpperCase = true;
            return this;
        }

        @Override
        public IConfigBuilder beginConfig() {
            return this;
        }

        @Override
        public IShapeBuilder endConfig() {
            return this;
        }

        @Override
        public IBuilder rect() {
            this.shape = new RectShape();
            return this;
        }

        @Override
        public IBuilder round() {
            this.shape = new OvalShape();
            return this;
        }

        @Override
        public IBuilder roundRect(int radius) {
            this.radius = radius;
            float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
            this.shape = new RoundRectShape(radii, null, null);
            return this;
        }

        @Override
        public IBuilder roundRect(int topLeftRadius, int topRightRadius, int bottomRightRadius, int bottomLeftRadius) {
            //this.radius = radius;
            float[] radii = {topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius};
            this.shape = new RoundRectShape(radii, null, null);
            return this;
        }

        @Override
        public ShapeDrawableBuilder buildRect(String text, int color) {
            rect();
            return build(text, color);
        }

        @Override
        public ShapeDrawableBuilder buildRoundRect(String text, int color, int radius) {
            roundRect(radius);
            return build(text, color);
        }

        @Override
        public ShapeDrawableBuilder buildRoundRect(String text, int color, int topLeftRadius, int topRightRadius, int bottomRightRadius, int bottomLeftRadius) {
            roundRect(topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius);
            return build(text, color);
        }

        @Override
        public ShapeDrawableBuilder buildRound(String text, int color) {
            round();
            return build(text, color);
        }

        @Override
        public ShapeDrawableBuilder build(String text, int color) {
            this.color = color;
            this.text = text;
            return new ShapeDrawableBuilder(this);
        }
    }



    public interface IBuilder {
        ShapeDrawableBuilder build(String text, int color);
    }

    public interface IShapeBuilder {

        IConfigBuilder beginConfig();

        IBuilder rect();

        IBuilder round();

        IBuilder roundRect(int radius);

        IBuilder roundRect(int topLeftRadius, int topRightRadius, int bottomRightRadius, int bottomLeftRadius);

        ShapeDrawableBuilder buildRect(String text, int color);

        ShapeDrawableBuilder buildRoundRect(String text, int color, int radius);

        ShapeDrawableBuilder buildRoundRect(String text, int color, int topLeftRadius, int topRightRadius, int bottomRightRadius, int bottomLeftRadius);

        ShapeDrawableBuilder buildRound(String text, int color);
    }
}