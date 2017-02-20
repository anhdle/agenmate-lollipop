package com.agenmate.lollipop.ui.layout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.agenmate.lollipop.R;
import com.agenmate.lollipop.util.ViewAnimUtils;

/**
 * Created by kincaid on 1/21/17.
 */

@CoordinatorLayout.DefaultBehavior(SheetBehavior.class)
public class SheetLayout extends FrameLayout {
    private static final int DEFAULT_ANIMATION_DURATION = 400;
    private static final int DEFAULT_FAB_SIZE = 56;
    private static final int FAB_CIRCLE = 0;
    private static final int FAB_EXPAND = 1;
    private static final int FULL = 0;
    private static final int BOTTOM = 1;
    private static final int CENTER = 2;

    private Animator fabSlideXAnim, fabSlideYAnim, sheetExpandAnim, sheetContractAnim;


    private OnTouchListener contractOnTouch = (v, event) -> {
        contractFab();
        return true;
    };

    private LinearLayout mFabExpandLayout;
    private ImageView mFab;

    private int animationDuration;
    private int mFabSize;
    private int currentMode;
    private int mFabType = FAB_CIRCLE;
    private boolean mAnimatingFab = false;

    @IntDef({ FAB_CIRCLE, FAB_EXPAND })
    private @interface Fab {}

    public SheetLayout(Context context) {
        super(context);
        init();
    }

    public SheetLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        loadAttributes(context, attrs);
    }

    public SheetLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        loadAttributes(context, attrs);
    }

    private void init() {
        inflate(getContext(), R.layout.sheet_layout, this);
        mFabExpandLayout = ((LinearLayout) findViewById(R.id.container));
    }

    private void loadAttributes(Context context, AttributeSet attrs) {
        TypedValue outValue = new TypedValue();
        Resources.Theme theme = context.getTheme();

        // use ?attr/colorPrimary as background color
        theme.resolveAttribute(R.attr.colorPrimary, outValue, true);

        TypedArray a =
                getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SheetLayout, 0, 0);

        int containerGravity;
        try {
            setColor(a.getColor(R.styleable.SheetLayout_sheet_color, outValue.data));


            containerGravity = a.getInteger(R.styleable.SheetLayout_sheet_container_gravity, 1);
            mFabSize = a.getInteger(R.styleable.SheetLayout_sheet_fab_type, DEFAULT_FAB_SIZE);
            currentMode = a.getInteger(R.styleable.SheetLayout_sheet_mode, FULL);
        } finally {
            a.recycle();
        }

        mFabExpandLayout.setGravity(getGravity(containerGravity));
        animationDuration = currentMode == FULL ? DEFAULT_ANIMATION_DURATION : DEFAULT_ANIMATION_DURATION / 2;
        if(currentMode == FULL){

            mFabType = FAB_EXPAND;
            mAnimatingFab = true;
            mFabExpandLayout.setAlpha(1f);
            mFabExpandLayout.setVisibility(View.VISIBLE);
        }
    }

    private LayoutParams getParams(){
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 70 );
        return params;
    }

    private int getGravity(int gravityEnum) {
        return (gravityEnum == 0 ? Gravity.START
                : gravityEnum == 1 ? Gravity.CENTER_HORIZONTAL : Gravity.END) | Gravity.CENTER_VERTICAL;
    }

    public void setColor(int color) {
        mFabExpandLayout.setBackgroundColor(color);
    }

    public void setFab(ImageView imageView) {
        mFab = imageView;
    }

    @Override public void addView(@NonNull View child) {
        if (canAddViewToContainer()) {
            mFabExpandLayout.addView(child);
        } else {
            super.addView(child);
        }
    }

    @Override public void addView(@NonNull View child, int width, int height) {
        if (canAddViewToContainer()) {
            mFabExpandLayout.addView(child, width, height);
        } else {
            super.addView(child, width, height);
        }
    }

    @Override public void addView(@NonNull View child, ViewGroup.LayoutParams params) {
        if (canAddViewToContainer()) {
            mFabExpandLayout.addView(child, params);
        } else {
            super.addView(child, params);
        }
    }

    @Override public void addView(@NonNull View child, int index, ViewGroup.LayoutParams params) {
        if (canAddViewToContainer()) {
            mFabExpandLayout.addView(child, index, params);
        } else {
            super.addView(child, index, params);
        }
    }

    /**
     * hide() and show() methods are useful for remembering the sheet state on screen rotation.
     */
    public void hide() {
        mFabExpandLayout.setVisibility(View.INVISIBLE);
        mFabType = FAB_CIRCLE;
    }

    public void show() {
        mFabExpandLayout.setVisibility(View.VISIBLE);
        mFabType = FAB_EXPAND;
    }

    private boolean canAddViewToContainer() {
        return mFabExpandLayout != null;
    }

    public void slideInFab() {
        if (mAnimatingFab) {
            return;
        }

        if (isFabExpanded()) {
            contractFab();
            return;
        }

        MarginLayoutParams lp = (MarginLayoutParams) mFab.getLayoutParams();
        float dy = mFab.getHeight() + lp.bottomMargin;
        if (mFab.getTranslationY() != dy) {
            return;
        }

        mAnimatingFab = true;
        mFab.setVisibility(View.VISIBLE);
        mFab.animate()
                .setStartDelay(0)
                .setDuration(200)
                .setInterpolator(new FastOutLinearInInterpolator())
                .translationY(0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override public void onAnimationEnd(final Animator animation) {
                        super.onAnimationEnd(animation);
                        mAnimatingFab = false;
                    }
                })
                .start();
    }

    public void slideOutFab() {
        if (mAnimatingFab) {
            return;
        }

        if (isFabExpanded()) {
            contractFab();
            return;
        }

        MarginLayoutParams lp = (MarginLayoutParams) mFab.getLayoutParams();
        if (mFab.getTranslationY() != 0f) {
            return;
        }

        mAnimatingFab = true;
        mFab.animate()
                .setStartDelay(0)
                .setDuration(200)
                .setInterpolator(new FastOutLinearInInterpolator())
                .translationY(mFab.getHeight() + lp.bottomMargin)
                .setListener(new AnimatorListenerAdapter() {
                    @Override public void onAnimationEnd(final Animator animation) {
                        super.onAnimationEnd(animation);
                        mAnimatingFab = false;
                        mFab.setVisibility(View.INVISIBLE);
                    }
                })
                .start();
    }


    public boolean isFabExpanded() {
        return mFabType == FAB_EXPAND;
    }

    public void expandFab() {

        mFabType = FAB_EXPAND;
        mAnimatingFab = true;

        mFabExpandLayout.setAlpha(0f);
        mFabExpandLayout.setVisibility(View.VISIBLE);

        int x = (int)centerX(mFab);
        int y = (int)centerY(mFab);

        float startRadius = getFabSizePx() / 2;
        float endRadius = calculateStartRadius(x, y);

        if(currentMode != FULL){
            float dx = getDx();
            float dy = getDy();

            fabSlideXAnim = ObjectAnimator.ofPropertyValuesHolder(mFab, PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, dx));
            fabSlideXAnim.setDuration(animationDuration);
            fabSlideXAnim.setInterpolator(new AccelerateInterpolator(1.0f));

            fabSlideYAnim = ObjectAnimator.ofPropertyValuesHolder(mFab, PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, dy));
            fabSlideYAnim.setDuration(animationDuration);
            fabSlideYAnim.setInterpolator(new DecelerateInterpolator(0.8f));
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            createExpandAnimPreLollipop(x, y, startRadius, endRadius);

            if(currentMode != FULL){
                fabSlideYAnim.addListener(new AnimatorListenerAdapter() {
                    @Override public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mFab.setVisibility(View.INVISIBLE);
                        mFab.setTranslationX(0f);
                        mFab.setTranslationY(0f);

                        // Play sheet expand animation asheeter slide animations finish.
                        sheetExpandAnim.start();
                    }
                });

                AnimatorSet animSet = new AnimatorSet();
                animSet.playTogether(fabSlideXAnim, fabSlideYAnim);

                animSet.start();
            } else sheetExpandAnim.start();

        } else {
            createExpandAnimLollipop(x, y, startRadius, endRadius);
            if(currentMode != FULL){
                fabSlideYAnim.addListener(new AnimatorListenerAdapter() {
                    @Override public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mFab.setVisibility(View.INVISIBLE);
                        mFab.setTranslationX(0f);
                        mFab.setTranslationY(0f);
                    }
                });
                AnimatorSet animSet = new AnimatorSet();
                animSet.playTogether(fabSlideXAnim, fabSlideYAnim, sheetExpandAnim);

                animSet.start();
            } else sheetExpandAnim.start();
        }
    }

    private void createExpandAnimPreLollipop(int x, int y, float startRadius, float endRadius) {
        sheetExpandAnim = ViewAnimUtils.createCircularReveal(mFabExpandLayout, x, y, startRadius, endRadius);
        sheetExpandAnim.setDuration(animationDuration);

        sheetExpandAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFabExpandLayout.setAlpha(1f);
                mFab.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimatingFab = false;
                if (mListener != null)
                    mListener.onFabAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) 
    private void createExpandAnimLollipop(int x, int y, float startRadius, float endRadius) {
        sheetExpandAnim = ViewAnimationUtils.createCircularReveal(mFabExpandLayout, x, y, startRadius, endRadius);
        sheetExpandAnim.setDuration(animationDuration);
        sheetExpandAnim.setStartDelay(animationDuration);
        sheetExpandAnim.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mFabExpandLayout.setAlpha(1f);
                mFab.setVisibility(INVISIBLE);
            }

            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimatingFab = false;
                if (mListener != null)
                    mListener.onFabAnimationEnd();
            }
        });
    }



    public void contractFab() {
        if (!isFabExpanded()) {
            return;
        }

        mFabType = FAB_CIRCLE;
        mAnimatingFab = true;
        mFab.setAlpha(0f);

        int x = (int) centerX(mFab);
        int y = (int) centerY(mFab);
        float endRadius = getFabSizePx() / 2;
        float startRadius = (float) Math.hypot(Math.max(x, mFabExpandLayout.getWidth() - x),
                Math.max(y, mFabExpandLayout.getHeight() - y));

        float dx = 0, dy = 0;
        if(currentMode != FULL){
            dx = getDx();
            dy = getDy();
            mFab.setTranslationX(dx);
            mFab.setTranslationY(dy);
        }


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if(currentMode != FULL){
                fabSlideXAnim = ObjectAnimator.ofPropertyValuesHolder(mFab, PropertyValuesHolder.ofFloat(View.TRANSLATION_X, dx, 0f));
                fabSlideXAnim.setDuration(animationDuration);
                fabSlideXAnim.setInterpolator(new DecelerateInterpolator(0.8f));

                fabSlideYAnim = ObjectAnimator.ofPropertyValuesHolder(mFab, PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, dy, 0f));
                fabSlideYAnim.setDuration(animationDuration);
                fabSlideYAnim.setInterpolator(new AccelerateInterpolator(1.0f));

                fabSlideYAnim.addListener(new AnimatorListenerAdapter() {
                    @Override public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mFabExpandLayout.setVisibility(View.INVISIBLE);
                        mFabExpandLayout.setAlpha(1f);
                        mAnimatingFab = false;
                    }
                });
            }

            createContractAnimPreLollipop(x, y, startRadius, endRadius);
            sheetContractAnim.start();
        } else {
            if(currentMode != FULL){
                fabSlideXAnim = ObjectAnimator.ofPropertyValuesHolder(mFab, PropertyValuesHolder.ofFloat(View.TRANSLATION_X, dx, 0f));
                fabSlideXAnim.setDuration(animationDuration);
                fabSlideXAnim.setInterpolator(new DecelerateInterpolator(0.8f));
                fabSlideXAnim.setStartDelay(animationDuration);

                fabSlideYAnim = ObjectAnimator.ofPropertyValuesHolder(mFab, PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, dy, 0f));
                fabSlideYAnim.setDuration(animationDuration);
                fabSlideYAnim.setInterpolator(new AccelerateInterpolator(1.0f));
                fabSlideYAnim.setStartDelay(animationDuration);

                fabSlideYAnim.addListener(new AnimatorListenerAdapter() {
                    @Override public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mFabExpandLayout.setVisibility(View.INVISIBLE);
                        mFabExpandLayout.setAlpha(1f);
                        mAnimatingFab = false;

                    }
                });
            }

            createContractAnimLollipop(x, y, startRadius, endRadius);

            if(currentMode != FULL) {
                AnimatorSet animSet = new AnimatorSet();
                animSet.playTogether(sheetContractAnim, fabSlideXAnim, fabSlideYAnim);

                animSet.start();
            } else sheetContractAnim.start();
        }
    }

    private void createContractAnimPreLollipop(int x, int y, float startRadius, float endRadius) {
        sheetContractAnim = ViewAnimUtils.createCircularReveal(mFabExpandLayout, x, y, startRadius, endRadius);
        sheetContractAnim.setDuration(animationDuration);
        sheetContractAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                mFab.setAlpha(1f);
                mFab.setVisibility(VISIBLE);
                mFabExpandLayout.setAlpha(0f);
                if(currentMode != FULL){
                    AnimatorSet animSet = new AnimatorSet();
                    animSet.playTogether(fabSlideXAnim, fabSlideYAnim);
                    animSet.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}

        });
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP) 
    private void createContractAnimLollipop(int x, int y, float startRadius, float endRadius) {
        sheetContractAnim = ViewAnimationUtils.createCircularReveal(mFabExpandLayout, x, y, startRadius, endRadius);
        sheetContractAnim.setDuration(animationDuration);

        sheetContractAnim.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mFab.setAlpha(1f);
                mFab.setVisibility(VISIBLE);
                mFabExpandLayout.setAlpha(0f);
            }
        });
    }


    private int getFabSizePx() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(mFabSize * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private float getDx(){
        return currentMode == FULL
                ? centerX(mFabExpandLayout) + mFab.getWidth() - centerX(mFab)
                : centerX(mFabExpandLayout) + getFabSizePx() - centerX(mFab);
    }
    private float getDy(){
        return currentMode == FULL
                ? centerY(mFabExpandLayout) / 20
                :getRelativeTop(mFabExpandLayout)
                -getRelativeTop(mFab)
                - (mFab.getHeight() - getFabSizePx()) / 2;
    }

    public int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView()) {
            return myView.getTop();
        } else {
            return myView.getTop() + getRelativeTop((View) myView.getParent());
        }
    }

    public interface OnFabAnimationEndListener {
        void onFabAnimationEnd();
    }

    OnFabAnimationEndListener mListener;

    public void setFabAnimationEndListener(OnFabAnimationEndListener eventListener) {
        mListener = eventListener;
    }

    public float centerX(View view) {
        return ViewCompat.getX(view) + view.getWidth() / 2f;
    }

    public float centerY(View view) {
        return ViewCompat.getY(view) + view.getHeight() / 2f;
    }

    public float calculateStartRadius(int x, int y){
        return (float) Math.hypot(
                Math.max(x, mFabExpandLayout.getWidth() - x),
                Math.max(y, mFabExpandLayout.getHeight() - y));

    }
}
