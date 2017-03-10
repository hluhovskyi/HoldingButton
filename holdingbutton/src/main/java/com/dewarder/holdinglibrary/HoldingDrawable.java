package com.dewarder.holdinglibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.animation.AccelerateInterpolator;

public class HoldingDrawable extends Drawable {

    private static final float MIN_EXPANDED_RADIUS_MULTIPLIER = 0.3f;
    private static final long DEFAULT_ANIMATION_DURATION_EXPAND = 150L;
    private static final long DEFAULT_ANIMATION_DURATION_COLLAPSE = 150L;
    private static final long DEFAULT_ANIMATION_DURATION_CANCEL = 150L;

    private Paint mPaint;
    private Paint mBitmapPaint;
    private Paint mSecondPaint;
    private Bitmap mIcon;
    private Bitmap mCancelIcon;

    private boolean mIsExpanded = false;
    private boolean mIsCancel = false;

    private ValueAnimator mAnimator;
    private ValueAnimator mCancelAnimator;
    private float mRadius = 120f;
    private float mSecondRadius = 140f;
    private float[] mExpandedScaleFactor = new float[1];

    private int mDefaultColor = Color.parseColor("#3949AB");
    private int mCancelColor = Color.parseColor("#e53935");
    private int mSecondAlpha = 100;

    private HoldingDrawableListener mListener;

    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mDefaultColor);

        mSecondPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSecondPaint.setColor(mDefaultColor);
        mSecondPaint.setAlpha(mSecondAlpha);

        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapPaint.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float centerX = canvas.getWidth() / 2f;
        float centerY = canvas.getHeight() / 2f;
        if (mIsExpanded) {
            if (mSecondRadius > mRadius) {
                canvas.drawCircle(centerX, centerY, mSecondRadius, mSecondPaint);
            }

            float currentRadius = mRadius * (MIN_EXPANDED_RADIUS_MULTIPLIER + (1 - MIN_EXPANDED_RADIUS_MULTIPLIER) * mExpandedScaleFactor[0]);
            canvas.drawCircle(centerX, centerY, currentRadius, mPaint);

            if (mIcon != null) {
                Bitmap icon = mIcon;
                if (mIsCancel && mCancelIcon != null) {
                    icon = mCancelIcon;
                }
                canvas.drawBitmap(icon, centerX - mIcon.getWidth() / 2, centerY - mIcon.getHeight() / 2, mBitmapPaint);
            }
        }
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) (mRadius * 2 + mSecondRadius * 2);
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) (mRadius * 2 + mSecondRadius * 2);
    }

    public void expand() {
        notifyOnBeforeExpand();
        mIsExpanded = true;
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = createExpandValueAnimator();
        mAnimator.start();
    }

    public void collapse() {
        notifyOnBeforeCollapse();
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = createCollapseValueAnimator();
        mAnimator.start();
    }

    public void reset() {
        mIsExpanded = false;
        mIsCancel = false;
        mPaint.setColor(mDefaultColor);
        mSecondPaint.setColor(mDefaultColor);
        mSecondPaint.setAlpha(mSecondAlpha);
    }

    public void setCancel(boolean isCancel) {
        if (this.mIsCancel != isCancel) {
            this.mIsCancel = isCancel;
            if (mCancelAnimator != null) {
                mCancelAnimator.cancel();
            }
            mCancelAnimator = createCancelValueAnimator();
            mCancelAnimator.start();
        }
    }

    public void setRadius(float radius) {
        mRadius = radius;
        invalidateSelf();
    }

    public float getRadius() {
        return mRadius;
    }

    @ColorInt
    public int getColor() {
        return mDefaultColor;
    }

    public void setColor(@ColorInt int color) {
        mDefaultColor = color;
        if (!mIsCancel) {
            mPaint.setColor(color);
            mSecondPaint.setColor(color);
            mSecondPaint.setAlpha(mSecondAlpha);
        }
        invalidateSelf();
    }

    @ColorInt
    public int getCancelColor() {
        return mCancelColor;
    }

    public void setCancelColor(int color) {
        mCancelColor = color;
        if (mIsCancel) {
            mPaint.setColor(color);
        }
        invalidateSelf();
    }

    public void setIcon(Bitmap bitmap) {
        mIcon = bitmap;
        invalidateSelf();
    }

    public void setCancelIcon(Bitmap bitmap) {
        mCancelIcon = bitmap;
        invalidateSelf();
    }

    @IntRange(from = 0, to = 255)
    public int getSecondAlpha() {
        return mSecondAlpha;
    }

    public void setSecondAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mSecondAlpha = alpha;
        invalidateSelf();
    }

    public float getSecondRadius() {
        return mSecondRadius;
    }

    public void setSecondRadius(float radius) {
        mSecondRadius = radius;
        invalidateSelf();
    }

    public void setListener(HoldingDrawableListener listener) {
        mListener = listener;
    }

    private ValueAnimator createExpandValueAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(DEFAULT_ANIMATION_DURATION_EXPAND);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mExpandedScaleFactor[0] = (float) valueAnimator.getAnimatedValue();
                invalidateSelf();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                notifyExpanded();
            }
        });
        return animator;
    }

    private ValueAnimator createCollapseValueAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
        animator.setDuration(DEFAULT_ANIMATION_DURATION_COLLAPSE);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mExpandedScaleFactor[0] = (float) valueAnimator.getAnimatedValue();
                invalidateSelf();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                notifyCollapsed();
            }
        });
        return animator;
    }

    private ValueAnimator createCancelValueAnimator() {
        final int from = mIsCancel ? mDefaultColor : mCancelColor;
        final int to = mIsCancel ? mCancelColor : mDefaultColor;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator a) {
                int color = ColorUtils.blend(from, to, (float) a.getAnimatedValue());
                mPaint.setColor(color);
                mSecondPaint.setColor(color);
                mSecondPaint.setAlpha(mSecondAlpha);
                invalidateSelf();
            }
        });
        animator.setDuration(DEFAULT_ANIMATION_DURATION_CANCEL);
        return animator;
    }

    private void notifyOnBeforeExpand() {
        if (mListener != null) {
            mListener.onBeforeExpand();
        }
    }

    private void notifyOnBeforeCollapse() {
        if (mListener != null) {
            mListener.onBeforeCollapse();
        }
    }

    private void notifyCollapsed() {
        if (mListener != null) {
            mListener.onCollapse();
        }
    }

    private void notifyExpanded() {
        if (mListener != null) {
            mListener.onExpand();
        }
    }
}
