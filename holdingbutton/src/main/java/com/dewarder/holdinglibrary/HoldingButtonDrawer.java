package com.dewarder.holdinglibrary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

interface HoldingButtonDrawer {

    void onAttach(
            @NonNull View holdingView,
            @NonNull HoldingButton holdingButton,
            @NonNull HoldingButtonLayout holdingButtonLayout);

    void onActionDown(
            @NonNull MotionEvent event,
            @NonNull HoldingButtonLayout.LayoutDirection layoutDirection,
            int offsetX,
            int offsetY);

    float onActionMove(
            @NonNull MotionEvent event,
            @NonNull HoldingButtonLayout.Direction direction,
            int offsetX,
            int offsetY);

    void onDispose(
            @NonNull View holdingView,
            @NonNull HoldingButton holdingButton,
            @NonNull HoldingButtonLayout holdingButtonLayout);
}

class PopupWindowHoldingButtonDrawer implements HoldingButtonDrawer {

    private PopupWindow mPopup;
    private FrameLayout mPopupContainer;

    private View mHoldingView;
    private HoldingButton mHoldingButton;
    private HoldingButtonLayout mHoldingButtonLayout;

    private int[] mHoldingButtonLayoutLocation = new int[2];
    private int[] mHoldingViewLocation = new int[2];

    private float mDeltaX;

    @Override
    public void onAttach(
            @NonNull View holdingView,
            @NonNull HoldingButton holdingButton,
            @NonNull HoldingButtonLayout holdingButtonLayout) {

        mHoldingView = holdingView;
        mHoldingButton = holdingButton;
        mHoldingButtonLayout = holdingButtonLayout;

        Context context = holdingButton.getContext();
        mPopupContainer = new FrameLayout(context);

        int buttonSize = mHoldingButton.getTotalRadius() * 2;
        mPopupContainer.addView(mHoldingButton, buttonSize, buttonSize);

        mPopup = new PopupWindow(
                mPopupContainer,
                WindowManager.LayoutParams.MATCH_PARENT,
                buttonSize
        );

        mHoldingView.getLocationInWindow(mHoldingViewLocation);
        mHoldingButtonLayout.getLocationInWindow(mHoldingButtonLayoutLocation);

        mPopup.showAtLocation(
                mHoldingButtonLayout,
                Gravity.NO_GRAVITY,
                mHoldingButtonLayoutLocation[0],
                mHoldingButtonLayoutLocation[1] - (buttonSize - mHoldingButtonLayout.getHeight()) / 2);
    }

    @Override
    public void onActionDown(
            @NonNull MotionEvent event,
            @NonNull HoldingButtonLayout.LayoutDirection layoutDirection,
            int offsetX,
            int offsetY) {

        int layoutWidth = mHoldingButtonLayout.getWidth();
        int viewCenterX = mHoldingViewLocation[0] + mHoldingView.getWidth() / 2;
        int circleCenterX = mHoldingButton.getWidth() / 2;
        float translationX = layoutDirection.calculateTranslationX(
                layoutWidth, viewCenterX, circleCenterX, offsetX);

        mHoldingButton.setTranslationX(translationX);

        mDeltaX = event.getRawX() - viewCenterX - offsetX;
    }

    @Override
    public float onActionMove(
            @NonNull MotionEvent event,
            @NonNull HoldingButtonLayout.Direction direction,
            int offsetX,
            int offsetY) {

        float circleCenterX = mHoldingButton.getWidth() / 2;
        float x = event.getRawX() - mDeltaX - circleCenterX;
        float slideOffset = direction.getSlideOffset(
                x,
                circleCenterX,
                mHoldingButtonLayoutLocation,
                mHoldingButtonLayout.getWidth(),
                mHoldingViewLocation,
                mHoldingView.getWidth(),
                offsetX
        );

        if (slideOffset >= 0 && slideOffset <= 1) {
            mHoldingButton.setX(x);
        }

        return slideOffset;
    }

    @Override
    public void onDispose(
            @NonNull View holdingView,
            @NonNull HoldingButton holdingButton,
            @NonNull HoldingButtonLayout holdingButtonLayout) {

        mPopup.dismiss();
        mPopupContainer.removeView(holdingButton);
        mPopup = null;
        mPopupContainer = null;
    }

    private static DisplayMetrics getDefaultDisplayMetrics(@NonNull Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }
}