package com.dewarder.holdinglibrary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class HoldingButtonLayout extends FrameLayout {

    private int mHoldingViewId = -1;
    private View mHoldingView;
    private Rect mHoldingViewRect = new Rect();
    private float mCancelOffset = 0.3f;
    private int mOffsetX = 0;
    private int mOffsetY = 0;
    private float mDeltaX;

    private View mHoldingCircle;
    private HoldingDrawable mHoldingDrawable;
    private int[] mViewLocation = new int[2];
    private int[] mHoldingViewLocation = new int[2];

    private boolean mAnimateHoldingView = true;
    private boolean mIsExpanded = false;

    private final DrawableListener mDrawableListener = new DrawableListener();
    private final List<HoldingButtonLayoutListener> mListeners = new ArrayList<>();

    public HoldingButtonLayout(@NonNull Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public HoldingButtonLayout(@NonNull Context context,
                               @Nullable AttributeSet attrs) {

        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public HoldingButtonLayout(@NonNull Context context,
                               @Nullable AttributeSet attrs,
                               @AttrRes int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init(context, attrs, 0, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HoldingButtonLayout(@NonNull Context context,
                               @Nullable AttributeSet attrs,
                               @AttrRes int defStyleAttr,
                               @StyleRes int defStyleRes) {

        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        mHoldingDrawable = new HoldingDrawable();
        mHoldingDrawable.setListener(mDrawableListener);

        if (attrs != null) {
            TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
                                                                         R.styleable.HoldingButtonLayout,
                                                                         defStyleAttr,
                                                                         defStyleRes);

            if (array.hasValue(R.styleable.HoldingButtonLayout_hbl_radius)) {
                mHoldingDrawable.setRadius(array.getDimensionPixelSize(R.styleable.HoldingButtonLayout_hbl_radius, 280));
            }

            if (array.hasValue(R.styleable.HoldingButtonLayout_hbl_icon)) {
                int drawableRes = array.getResourceId(R.styleable.HoldingButtonLayout_hbl_icon, 0);
                mHoldingDrawable.setIcon(BitmapFactory.decodeResource(getResources(), drawableRes));
            }

            if (array.hasValue(R.styleable.HoldingButtonLayout_hbl_cancel_icon)) {
                int drawableRes = array.getResourceId(R.styleable.HoldingButtonLayout_hbl_cancel_icon, 0);
                mHoldingDrawable.setCancelIcon(BitmapFactory.decodeResource(getResources(), drawableRes));
            }

            if (array.hasValue(R.styleable.HoldingButtonLayout_hbl_offset_x)) {
                mOffsetX = array.getDimensionPixelSize(R.styleable.HoldingButtonLayout_hbl_offset_x, 0);
            }

            if (array.hasValue(R.styleable.HoldingButtonLayout_hbl_offset_y)) {
                mOffsetY = array.getDimensionPixelSize(R.styleable.HoldingButtonLayout_hbl_offset_y, 0);
            }

            if (array.hasValue(R.styleable.HoldingButtonLayout_hbl_holding_view)) {
                mHoldingViewId = array.getResourceId(R.styleable.HoldingButtonLayout_hbl_holding_view, -1);
            }

            if (array.hasValue(R.styleable.HoldingButtonLayout_hbl_animate_holding_view)) {
                mAnimateHoldingView = array.getBoolean(R.styleable.HoldingButtonLayout_hbl_animate_holding_view, true);
            }

            if (array.hasValue(R.styleable.HoldingButtonLayout_hbl_color)) {
                mHoldingDrawable.setColor(array.getColor(R.styleable.HoldingButtonLayout_hbl_color, 0));
            }

            if (array.hasValue(R.styleable.HoldingButtonLayout_hbl_cancel_color)) {
                mHoldingDrawable.setCancelColor(array.getColor(R.styleable.HoldingButtonLayout_hbl_cancel_color, 0));
            }

            if (array.hasValue(R.styleable.HoldingButtonLayout_hbl_second_radius)) {
                mHoldingDrawable.setSecondRadius(array.getDimension(R.styleable.HoldingButtonLayout_hbl_second_radius, 0));
            }

            if (array.hasValue(R.styleable.HoldingButtonLayout_hbl_second_alpha)) {
                float alphaMultiplier = array.getFloat(R.styleable.HoldingButtonLayout_hbl_second_alpha, 1);
                if (alphaMultiplier < 0 && alphaMultiplier > 1) {
                    throw new IllegalStateException("Second alpha value must be between 0 and 1");
                }
                mHoldingDrawable.setSecondAlpha((int) (255 * alphaMultiplier));
            }

            if (array.hasValue(R.styleable.HoldingButtonLayout_hbl_cancel_offset)) {
                float cancelOffset = array.getFloat(R.styleable.HoldingButtonLayout_hbl_cancel_offset, 1);
                if (cancelOffset < 0 && cancelOffset > 1) {
                    throw new IllegalStateException("Cancel offset must be between 0 and 1");
                }
                mCancelOffset = cancelOffset;
            }

            array.recycle();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                return isViewTouched(mHoldingView, ev);
            }
        }

        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (isViewTouched(mHoldingView, event)) {
                    mHoldingView.getLocationInWindow(mHoldingViewLocation);
                    getLocationInWindow(mViewLocation);

                    int centerX = mHoldingViewLocation[0] + mHoldingView.getWidth() / 2;
                    int centerY = mHoldingViewLocation[1] + mHoldingView.getHeight() / 2;

                    float translationX = centerX - mHoldingCircle.getWidth() / 2f + mOffsetX;
                    float translationY = centerY - mHoldingCircle.getHeight() / 2f + mOffsetY;

                    mHoldingCircle.setTranslationX(translationX);
                    mHoldingCircle.setTranslationY(translationY);

                    mDeltaX = mHoldingView.getX() - event.getRawX() + mHoldingView.getWidth() / 2 + mOffsetX;

                    mHoldingDrawable.expand();
                    mIsExpanded = true;
                    return true;
                }
            }

            case MotionEvent.ACTION_MOVE: {
                if (mIsExpanded) {
                    float circleCenterX = mHoldingCircle.getWidth() / 2;
                    float x = event.getRawX() + mDeltaX - circleCenterX;
                    float slideOffset = 1 - (x + circleCenterX) / getWidth();
                    if (slideOffset >= 0 && slideOffset <= 1) {
                        mHoldingCircle.animate().x(x).setDuration(0).start();

                        boolean isCancel = slideOffset > mCancelOffset;
                        mHoldingDrawable.setCancel(isCancel);
                        notifyOnOffsetChanged(slideOffset, isCancel);
                    }
                    return true;
                }
            }

            case MotionEvent.ACTION_UP: {
                if (mIsExpanded) {
                    mHoldingDrawable.collapse();
                    mIsExpanded = false;
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getDecorView().addView(mHoldingCircle, mHoldingDrawable.getIntrinsicWidth(), mHoldingDrawable.getIntrinsicHeight());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mHoldingView == null && mHoldingViewId != -1) {
            mHoldingView = findViewById(mHoldingViewId);
        }

        if (mHoldingView == null) {
            throw new IllegalStateException("Holding view doesn't set. Call setHoldingView before inflate");
        }

        mHoldingCircle = new View(getContext());
        mHoldingCircle.setVisibility(INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mHoldingCircle.setBackground(mHoldingDrawable);
        } else {
            mHoldingCircle.setBackgroundDrawable(mHoldingDrawable);
        }
    }

    protected ViewGroup getDecorView() {
        //Try to fetch DecorView from context
        if (getContext() instanceof Activity) {
            View decor = ((Activity) getContext()).getWindow().getDecorView();

            if (decor instanceof ViewGroup) {
                return (ViewGroup) decor;
            }
        }

        //Try to fetch DecorView from parents
        ViewGroup view = this;
        while (view.getParent() != null && view.getParent() instanceof ViewGroup) {
            view = (ViewGroup) view.getParent();
        }
        return view;
    }

    public void addListener(HoldingButtonLayoutListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(HoldingButtonLayoutListener listener) {
        mListeners.remove(listener);
    }

    private boolean isViewTouched(View view, MotionEvent event) {
        view.getDrawingRect(mHoldingViewRect);
        view.getLocationOnScreen(mHoldingViewLocation);
        mHoldingViewRect.offset(mHoldingViewLocation[0], mHoldingViewLocation[1]);
        return mHoldingViewRect.contains((int) event.getRawX(), (int) event.getRawY());
    }

    @ColorInt
    public int getColor() {
        return mHoldingDrawable.getColor();
    }

    public void setColor(@ColorInt int color) {
        mHoldingDrawable.setColor(color);
    }

    @ColorInt
    public int getCancelColor() {
        return mHoldingDrawable.getCancelColor();
    }

    public void setCancelColor(@ColorInt int color) {
        mHoldingDrawable.setCancelColor(color);
    }

    @FloatRange(from = 0, to = 1)
    public float getCancelOffset() {
        return mCancelOffset;
    }

    public void setCancelOffset(@FloatRange(from = 0, to = 1) float offset) {
        mCancelOffset = offset;
    }

    public float getRadius() {
        return mHoldingDrawable.getRadius();
    }

    public void setRadius(float radius) {
        mHoldingDrawable.setRadius(radius);
    }

    public float getSecondRadius() {
        return mHoldingDrawable.getSecondRadius();
    }

    public void setSecondRadius(float radius) {
        mHoldingDrawable.setSecondRadius(radius);
    }

    public void setIcon(@DrawableRes int drawableRes) {
        setIcon(BitmapFactory.decodeResource(getResources(), drawableRes));
    }

    public void setIcon(Bitmap bitmap) {
        mHoldingDrawable.setIcon(bitmap);
    }

    public View getHoldingView() {
        return mHoldingView;
    }

    public void setHoldingView(View holdingView) {
        mHoldingView = holdingView;
    }

    public void setAnimateHoldingView(boolean animate) {
        mAnimateHoldingView = animate;
    }

    public boolean isAnimateHoldingView() {
        return mAnimateHoldingView;
    }

    private void notifyOnBeforeExpand() {
        for (HoldingButtonLayoutListener listener : mListeners) {
            listener.onBeforeExpand();
        }
    }

    private void notifyOnBeforeCollapse() {
        for (HoldingButtonLayoutListener listener : mListeners) {
            listener.onBeforeCollapse();
        }
    }

    private void notifyOnExpand() {
        for (HoldingButtonLayoutListener listener : mListeners) {
            listener.onExpand();
        }
    }

    private void notifyOnCollapse() {
        for (HoldingButtonLayoutListener listener : mListeners) {
            listener.onCollapse();
        }
    }

    private void notifyOnOffsetChanged(float offset, boolean isCancel) {
        for (HoldingButtonLayoutListener listener : mListeners) {
            listener.onOffsetChanged(offset, isCancel);
        }
    }

    private class DrawableListener implements HoldingDrawableListener {

        @Override
        public void onBeforeExpand() {
            notifyOnBeforeExpand();
            mHoldingDrawable.reset();
            mHoldingCircle.setVisibility(VISIBLE);

            if (mAnimateHoldingView) {
                mHoldingView.setVisibility(INVISIBLE);
            }
        }

        @Override
        public void onBeforeCollapse() {
            notifyOnBeforeCollapse();
        }

        @Override
        public void onExpand() {
            notifyOnExpand();
        }

        @Override
        public void onCollapse() {
            notifyOnCollapse();
            mHoldingCircle.setVisibility(GONE);

            if (mAnimateHoldingView) {
                mHoldingView.setAlpha(0f);
                mHoldingView.setScaleY(0.8f);
                mHoldingView.setVisibility(VISIBLE);
                mHoldingView.animate()
                        .alpha(1f)
                        .scaleY(1f)
                        .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                        .start();
            }
        }
    }
}
