package com.agenmate.lollipop.ui.layout;

import android.view.ViewGroup;

import com.agenmate.lollipop.util.CircularRevealManager;

/**
 * Indicator for internal API that {@link ViewGroup} support
 * Circular Reveal animation
 */
public interface RevealViewGroup {

  /**
   * @return Bridge between view and circular reveal animation
   */
  CircularRevealManager getViewRevealManager();
}