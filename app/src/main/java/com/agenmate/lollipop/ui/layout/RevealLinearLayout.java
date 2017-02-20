package com.agenmate.lollipop.ui.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.agenmate.lollipop.util.CircularRevealManager;


public class RevealLinearLayout extends LinearLayout implements RevealViewGroup {
  private CircularRevealManager manager;

  public RevealLinearLayout(Context context) {
    this(context, null);
  }

  public RevealLinearLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RevealLinearLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs);
    manager = new CircularRevealManager();
  }

  @Override protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    try {
      canvas.save();

      manager.transform(canvas, child);
      return super.drawChild(canvas, child, drawingTime);
    } finally {
      canvas.restore();
    }
  }

  @Override public CircularRevealManager getViewRevealManager() {
    return manager;
  }
}
