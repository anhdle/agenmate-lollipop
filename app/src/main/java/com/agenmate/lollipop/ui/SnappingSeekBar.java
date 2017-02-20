package com.agenmate.lollipop.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.agenmate.lollipop.R;
import com.agenmate.lollipop.util.ViewUtils;
public class SnappingSeekBar extends RelativeLayout implements SeekBar.OnSeekBarChangeListener {
    public static final int         NOT_INITIALIZED_THUMB_POSITION  = -1;
    private Context                 mContext;
    private SeekBar                 mSeekBar;
    private int                     mItemsAmount;
    private int                     mFromProgress;
    private int                     mThumbPosition                  = NOT_INITIALIZED_THUMB_POSITION;
    private int                     mToProgress;
    private String[]                mItems                          = new String[0];
    private int                     mProgressDrawableId;
    private int                     mIndicatorDrawableId;
    private int                     mProgressColor;
    private int                     mIndicatorColor;
    private int                     mThumbnailColor;
    private int                     mTextIndicatorColor;
    private float                   mTextIndicatorTopMargin;
    private int                     mTextStyleId;
    private float                   mTextSize;
    private float                   mIndicatorSize;
    private float                   mDensity;
    private Drawable                mProgressDrawable;
    private Drawable yellowBalloon, orangeBalloon, redBalloon;
    private OnItemSelectionListener mOnItemSelectionListener;

    public SnappingSeekBar(final Context context) {
        super(context);
        mContext = context;
        initDensity();
        initDefaultValues();
        initViewsAfterLayoutPrepared();
    }

    private void initDensity() {
        mDensity = mContext.getResources().getDisplayMetrics().density;
    }

    private void initDefaultValues() {
        mProgressDrawableId = R.drawable.progress_bar;
        mIndicatorDrawableId = R.drawable.circle_background;
        mProgressColor = Color.WHITE;
        mIndicatorColor = Color.WHITE;
        mThumbnailColor = Color.WHITE;
        mTextIndicatorColor = Color.WHITE;
        mTextIndicatorTopMargin = 35 * mDensity;
        mTextSize = 12 * mDensity;
        mIndicatorSize = 11.3f * mDensity;
    }

    private void initViewsAfterLayoutPrepared() {
        ViewUtils.waitForLayoutPrepared(this, new ViewUtils.LayoutPreparedListener() {
            @Override
            public void onLayoutPrepared(final View preparedView) {
                initViews();
            }
        });
    }

    public SnappingSeekBar(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initDensity();
        initDrawables();
        setItemsAmount(3);
        initIndicatorAttributes();
        initTextAttributes();
        initColors();
        initViews();
    }


    private void initDrawables() {
        mProgressDrawableId = R.drawable.progress_bar;

        mIndicatorDrawableId = R.drawable.circle_background;
    }




    public void setItems(final String[] items) {
        if(items.length > 1) {
            mItems = items;
            mItemsAmount = mItems.length;
        } else {
            throw new IllegalStateException("SnappingSeekBar has to contain at least 2 items");
        }
    }

    public void setItemsAmount(final int itemsAmount) {
        if(itemsAmount > 1) {
            mItemsAmount = itemsAmount;
        } else {
            throw new IllegalStateException("SnappingSeekBar has to contain at least 2 items");
        }
    }

    private void initIndicatorAttributes() {
        mIndicatorSize = 11.3f * mDensity;
    }

    private void initTextAttributes() {
        mTextIndicatorTopMargin = 35 * mDensity;
        mTextStyleId = 0;
        mTextSize = 12 * mDensity;
    }

    private void initColors() {
        mProgressColor = Color.WHITE;
        mIndicatorColor = Color.WHITE;
        mThumbnailColor = Color.WHITE;
        mTextIndicatorColor = Color.WHITE;
    }

    public void initViews() {
        initSeekBar();
        initIndicators();
    }

    private void initSeekBar() {
        final LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mSeekBar = new SeekBar(mContext);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setLayoutParams(params);
        setDrawablesToSeekBar();
        addView(mSeekBar, params);
    }

    private void setDrawablesToSeekBar() {
        mProgressDrawable = ContextCompat.getDrawable(getContext(), mProgressDrawableId);
        redBalloon = ContextCompat.getDrawable(getContext(), R.drawable.balloon_thumb_red);
        yellowBalloon = ContextCompat.getDrawable(getContext(), R.drawable.balloon_thumb_yellow);
        orangeBalloon = ContextCompat.getDrawable(getContext(), R.drawable.balloon_thumb_orange);
        ViewUtils.setColor(mProgressDrawable, mProgressColor);
        ViewUtils.setColor(redBalloon, mThumbnailColor);
        mSeekBar.setProgressDrawable(mProgressDrawable);
        mSeekBar.setThumb(redBalloon);
        final int thumbnailWidth = redBalloon.getIntrinsicWidth();
        mSeekBar.setPadding(thumbnailWidth / 2, 0, thumbnailWidth / 2, 0);
    }

    private void initIndicators() {
        ViewUtils.waitForLayoutPrepared(mSeekBar, preparedView -> {
            final int seekBarWidth = preparedView.getWidth();
            initIndicators(seekBarWidth);
        });
    }

    private void initIndicators(final int seekBarWidth) {
        for(int i = 0; i < mItemsAmount; i++) {
            addCircleIndicator(seekBarWidth, i);
            addTextIndicatorIfNeeded(seekBarWidth, i);
        }
    }

    private void addCircleIndicator(final int seekBarWidth, final int index) {
        final int thumbnailWidth = redBalloon.getIntrinsicWidth();
        final int sectionFactor = 100 / (mItemsAmount - 1);
        final float seekBarWidthWithoutThumbOffset = seekBarWidth - thumbnailWidth;
        final LayoutParams indicatorParams = new LayoutParams((int) mIndicatorSize, (int) mIndicatorSize);
        final View indicator = new View(mContext);
        indicator.setBackgroundResource(mIndicatorDrawableId);
        ViewUtils.setColor(indicator.getBackground(), mIndicatorColor);
        indicatorParams.leftMargin = (int) (seekBarWidthWithoutThumbOffset / 100 * index * sectionFactor + thumbnailWidth / 2 - mIndicatorSize / 2);
        indicatorParams.topMargin = redBalloon.getIntrinsicHeight() / 2 - (int) (mIndicatorSize / 2);
        addView(indicator, indicatorParams);
    }

    private void addTextIndicatorIfNeeded(final int completeSeekBarWidth, final int index) {
        if(mItems.length == mItemsAmount) {
            addTextIndicator(completeSeekBarWidth, index);
        }
    }

    private void addTextIndicator(final int completeSeekBarWidth, final int index) {
        final int thumbnailWidth = redBalloon.getIntrinsicWidth();
        final int sectionFactor = 100 / (mItemsAmount - 1);
        final float seekBarWidthWithoutThumbOffset = completeSeekBarWidth - thumbnailWidth;
        final LayoutParams textParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textIndicator = new TextView(mContext);
        final int numberLeftMargin = (int) (seekBarWidthWithoutThumbOffset / 100 * index * sectionFactor + thumbnailWidth / 2);
        textIndicator.setText(mItems[index]);
        textIndicator.setTextSize(mTextSize / mDensity);
        textIndicator.setTextColor(mTextIndicatorColor);
        if (Build.VERSION.SDK_INT < 23) {
            textIndicator.setTextAppearance(mContext, mTextStyleId);
        } else {
            textIndicator.setTextAppearance(mTextStyleId);
        }
        textParams.topMargin = (int) mTextIndicatorTopMargin;
        addView(textIndicator, textParams);
        ViewUtils.waitForLayoutPrepared(textIndicator, createTextIndicatorLayoutPreparedListener(numberLeftMargin));
    }

    private ViewUtils.LayoutPreparedListener createTextIndicatorLayoutPreparedListener(final int numberLeftMargin) {
        return new ViewUtils.LayoutPreparedListener() {
            @Override
            public void onLayoutPrepared(final View preparedView) {
                final int layoutWidth = getWidth() - getPaddingRight();
                final int viewWidth = preparedView.getWidth();
                final int leftMargin = numberLeftMargin - viewWidth / 2;
                final int paddingLeft = getPaddingLeft();
                final int finalMargin = leftMargin < paddingLeft ? paddingLeft : leftMargin + viewWidth > layoutWidth ? layoutWidth - viewWidth : leftMargin;
                ViewUtils.setLeftMargin(preparedView, finalMargin);
            }
        };
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
        mToProgress = progress;
        updateColor(progress);
        initThumbPosition(progress, fromUser);
        handleSetFromProgress(progress);
    }

    private void updateColor(int progress){
        if(progress == 0){
            mSeekBar.setThumb(yellowBalloon);
            ViewUtils.setColor(mProgressDrawable, ContextCompat.getColor(getContext(), R.color.md_yellow_700));
        }
        else if(progress == 50){
            mSeekBar.setThumb(orangeBalloon);
            ViewUtils.setColor(mProgressDrawable, ContextCompat.getColor(getContext(), R.color.md_orange_700));
        }
        else if(progress == 100){
            mSeekBar.setThumb(redBalloon);
            ViewUtils.setColor(mProgressDrawable, ContextCompat.getColor(getContext(), R.color.md_red_700));
        }
    }

    private void initThumbPosition(final int progress, final boolean fromUser) {
        if(mThumbPosition == NOT_INITIALIZED_THUMB_POSITION && fromUser) {
            mThumbPosition = progress;
        }
    }

    private void handleSetFromProgress(final int progress) {
        final int slidingDelta = progress - mThumbPosition;
        if(slidingDelta > 1 || slidingDelta < -1) {
            mFromProgress = progress;
        }
    }

    private void handleSnapToClosestValue() {
        final float sectionLength = 100 / (mItemsAmount - 1);
        final int selectedSection = (int) ((mToProgress / sectionLength) + 0.5);
        final int valueToSnap = (int) (selectedSection * sectionLength);
        animateProgressBar(valueToSnap);
        invokeItemSelected(selectedSection);
    }

    private void animateProgressBar(final int toProgress) {
        final ProgressBarAnimation anim = new ProgressBarAnimation(mSeekBar, mFromProgress, toProgress);
        anim.setDuration(200);
        startAnimation(anim);
    }

    private void invokeItemSelected(final int selectedSection) {
        if(mOnItemSelectionListener != null) {
            mOnItemSelectionListener.onItemSelected(selectedSection, getItemString(selectedSection));
        }
    }

    private String getItemString(final int index) {
        if(mItems.length > index) {
            return mItems[index];
        }
        return "";
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
        mFromProgress = mSeekBar.getProgress();
        mThumbPosition = NOT_INITIALIZED_THUMB_POSITION;
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
        handleSnapToClosestValue();
    }

    public Drawable getProgressDrawable() {
        return mProgressDrawable;
    }

    public Drawable getThumb() {
        return redBalloon;
    }

    public int getProgress() {
        return mSeekBar.getProgress();
    }

    public void setProgress(final int progress) {
        mToProgress = progress;
        updateColor(progress);
        handleSnapToClosestValue();
    }

    public void setProgressToIndex(final int index) {
        mToProgress = getProgressForIndex(index);
        mSeekBar.setProgress(mToProgress);
    }

    private int getProgressForIndex(final int index) {
        final float sectionLength = 100 / (mItemsAmount - 1);
        return (int) (index * sectionLength);
    }

    public void setProgressToIndexWithAnimation(final int index) {
        mToProgress = getProgressForIndex(index);
        animateProgressBar(mToProgress);
    }

    public int getSelectedItemIndex() {
        final float sectionLength = 100 / (mItemsAmount - 1);
        return (int) ((mToProgress / sectionLength) + 0.5);
    }

    public void setOnItemSelectionListener(final OnItemSelectionListener listener) {
        mOnItemSelectionListener = listener;
    }

    public void setProgressDrawable(final int progressDrawableId) {
        mProgressDrawableId = progressDrawableId;
    }

    public void setIndicatorDrawable(final int indicatorDrawableId) {
        mIndicatorDrawableId = indicatorDrawableId;
    }

    public void setProgressColor(final int progressColor) {
        mProgressColor = progressColor;
    }

    public void setIndicatorColor(final int indicatorColor) {
        mIndicatorColor = indicatorColor;
    }

    public void setThumbnailColor(final int thumbnailColor) {
        mThumbnailColor = thumbnailColor;
    }

    public void setTextIndicatorColor(final int textIndicatorColor) {
        mTextIndicatorColor = textIndicatorColor;
    }

    public void setTextIndicatorTopMargin(final float textIndicatorTopMargin) {
        mTextIndicatorTopMargin = textIndicatorTopMargin;
    }

    public void setTextStyleId(final int textStyleId) {
        mTextStyleId = textStyleId;
    }

    public void setTextSize(final int textSize) {
        mTextSize = mDensity * textSize;
    }

    public void setIndicatorSize(final int indicatorSize) {
        mIndicatorSize = mDensity * indicatorSize;
    }

    public interface OnItemSelectionListener {
        public void onItemSelected(final int itemIndex, final String itemString);
    }
}