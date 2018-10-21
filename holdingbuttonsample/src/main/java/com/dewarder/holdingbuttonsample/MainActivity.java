/*
 * Copyright (C) 2017 Artem Glugovsky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dewarder.holdingbuttonsample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dewarder.holdinglibrary.HoldingButtonLayout;
import com.dewarder.holdinglibrary.HoldingButtonLayoutListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements HoldingButtonLayoutListener {

    private static final DateFormat mFormatter = new SimpleDateFormat("mm:ss:SS");
    private static final float SLIDE_TO_CANCEL_ALPHA_MULTIPLIER = 2.5f;
    private static final long TIME_INVALIDATION_FREQUENCY = 50L;

    private HoldingButtonLayout mHoldingButtonLayout;
    private TextView mTime;
    private EditText mInput;
    private View mSlideToCancel;

    private int mAnimationDuration;
    private ViewPropertyAnimator mTimeAnimator;
    private ViewPropertyAnimator mSlideToCancelAnimator;
    private ViewPropertyAnimator mInputAnimator;

    private long mStartTime;
    private Runnable mTimerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHoldingButtonLayout = findViewById(R.id.input_holder);
        mHoldingButtonLayout.addListener(this);

        mTime = findViewById(R.id.time);
        mInput = findViewById(R.id.input);
        mSlideToCancel = findViewById(R.id.slide_to_cancel);

        mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    private Random random = new Random();
    private Handler handler = new Handler(Looper.getMainLooper());

    private void postAmplitude() {
        mHoldingButtonLayout.setAmplitude(random.nextFloat());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                postAmplitude();
            }
        }, 200);
    }

    @Override
    public void onBeforeExpand() {
        cancelAllAnimations();

        mSlideToCancel.setTranslationX(0f);
        mSlideToCancel.setAlpha(0f);
        mSlideToCancel.setVisibility(View.VISIBLE);
        mSlideToCancelAnimator = mSlideToCancel.animate().alpha(1f).setDuration(mAnimationDuration);
        mSlideToCancelAnimator.start();

        mInputAnimator = mInput.animate().alpha(0f).setDuration(mAnimationDuration);
        mInputAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mInput.setVisibility(View.INVISIBLE);
                mInputAnimator.setListener(null);
            }
        });
        mInputAnimator.start();

        mTime.setTranslationY(mTime.getHeight());
        mTime.setAlpha(0f);
        mTime.setVisibility(View.VISIBLE);
        mTimeAnimator = mTime.animate().translationY(0f).alpha(1f).setDuration(mAnimationDuration);
        mTimeAnimator.start();

    }

    @Override
    public void onExpand() {
        mStartTime = System.currentTimeMillis();
        invalidateTimer();
       // postAmplitude();
    }

    @Override
    public void onBeforeCollapse() {
        cancelAllAnimations();

        mSlideToCancelAnimator = mSlideToCancel.animate().alpha(0f).setDuration(mAnimationDuration);
        mSlideToCancelAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSlideToCancel.setVisibility(View.INVISIBLE);
                mSlideToCancelAnimator.setListener(null);
            }
        });
        mSlideToCancelAnimator.start();

        mInput.setAlpha(0f);
        mInput.setVisibility(View.VISIBLE);
        mInputAnimator = mInput.animate().alpha(1f).setDuration(mAnimationDuration);
        mInputAnimator.start();

        mTimeAnimator = mTime.animate().translationY(mTime.getHeight()).alpha(0f).setDuration(mAnimationDuration);
        mTimeAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTime.setVisibility(View.INVISIBLE);
                mTimeAnimator.setListener(null);
            }
        });
        mTimeAnimator.start();
    }

    @Override
    public void onCollapse(boolean isCancel) {
        stopTimer();
        if (isCancel) {
            Toast.makeText(this, "Action canceled! Time " + getFormattedTime(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Action submitted! Time " + getFormattedTime(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onOffsetChanged(float offset, boolean isCancel) {
        mSlideToCancel.setTranslationX(-mHoldingButtonLayout.getWidth() * offset);
        mSlideToCancel.setAlpha(1 - SLIDE_TO_CANCEL_ALPHA_MULTIPLIER * offset);
    }

    private void invalidateTimer() {
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                mTime.setText(getFormattedTime());
                invalidateTimer();
            }
        };

        mTime.postDelayed(mTimerRunnable, TIME_INVALIDATION_FREQUENCY);
    }

    private void stopTimer() {
        if (mTimerRunnable != null) {
            mTime.getHandler().removeCallbacks(mTimerRunnable);
        }
    }

    private void cancelAllAnimations() {
        if (mInputAnimator != null) {
            mInputAnimator.cancel();
        }

        if (mSlideToCancelAnimator != null) {
            mSlideToCancelAnimator.cancel();
        }

        if (mTimeAnimator != null) {
            mTimeAnimator.cancel();
        }
    }

    private String getFormattedTime() {
        return mFormatter.format(new Date(System.currentTimeMillis() - mStartTime));
    }
}
