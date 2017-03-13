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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.EditText;
import android.widget.Toast;

import com.dewarder.holdinglibrary.HoldingButtonLayout;
import com.dewarder.holdinglibrary.HoldingButtonLayoutListener;

public class MainActivity extends AppCompatActivity implements HoldingButtonLayoutListener {

    private static final float SLIDE_TO_CANCEL_ALPHA_MULTIPLIER = 2.5f;

    private HoldingButtonLayout mHoldingButtonLayout;
    private EditText mInput;
    private View mSlideToCancel;

    private int mAnimationDuration;
    private ViewPropertyAnimator mSlideToCancelAnimator;
    private ViewPropertyAnimator mInputAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHoldingButtonLayout = (HoldingButtonLayout) findViewById(R.id.input_holder);
        mHoldingButtonLayout.addListener(this);

        mInput = (EditText) findViewById(R.id.input);
        mSlideToCancel = findViewById(R.id.slide_to_cancel);

        mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public void onBeforeExpand() {
        if (mInputAnimator != null) {
            mInputAnimator.cancel();
        }

        if (mSlideToCancelAnimator != null) {
            mSlideToCancelAnimator.cancel();
        }

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
    }

    @Override
    public void onExpand() {

    }

    @Override
    public void onBeforeCollapse() {
        if (mInputAnimator != null) {
            mInputAnimator.cancel();
        }

        if (mSlideToCancelAnimator != null) {
            mSlideToCancelAnimator.cancel();
        }

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
    }

    @Override
    public void onCollapse(boolean isCancel) {
        if (isCancel) {
            Toast.makeText(this, "Action canceled!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Action submited!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onOffsetChanged(float offset, boolean isCancel) {
        mSlideToCancel.setTranslationX(-mHoldingButtonLayout.getWidth() * offset);
        mSlideToCancel.setAlpha(1 - SLIDE_TO_CANCEL_ALPHA_MULTIPLIER * offset);
    }
}
