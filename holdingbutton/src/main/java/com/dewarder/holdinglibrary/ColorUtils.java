package com.dewarder.holdinglibrary;

import android.graphics.Color;

public final class ColorUtils {

    public static int blend(int from, int to, float ratio) {
        float inverseRatio = 1f - ratio;

        float a = Color.alpha(to) * ratio + Color.alpha(from) * inverseRatio;
        float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }
}
