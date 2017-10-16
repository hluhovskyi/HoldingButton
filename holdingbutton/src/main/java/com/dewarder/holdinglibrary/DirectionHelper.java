package com.dewarder.holdinglibrary;

import android.os.Build;
import android.view.View;

import com.dewarder.holdinglibrary.HoldingButtonLayout.Direction;
import com.dewarder.holdinglibrary.HoldingButtonLayout.LayoutDirection;

final class DirectionHelper {

    static LayoutDirection resolveLayoutDirection(HoldingButtonLayout view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return LayoutDirection.LTR;
        }

        int rawDirection = view.getResources().getConfiguration().getLayoutDirection();
        if (rawDirection == View.LAYOUT_DIRECTION_LTR) {
            return LayoutDirection.LTR;
        } else {
            return LayoutDirection.RTL;
        }
    }

    static Direction resolveDefaultSlidingDirection(HoldingButtonLayout view) {
        if (resolveLayoutDirection(view) == LayoutDirection.LTR) {
            return Direction.START;
        } else {
            return Direction.END;
        }
    }

    static Direction adaptSlidingDirection(HoldingButtonLayout view, Direction direction) {
        if (resolveLayoutDirection(view) == LayoutDirection.LTR) {
            return direction;
        } else {
            return direction.toRtl();
        }
    }
}
