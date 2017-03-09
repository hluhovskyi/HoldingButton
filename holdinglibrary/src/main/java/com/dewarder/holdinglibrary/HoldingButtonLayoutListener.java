package com.dewarder.holdinglibrary;

public interface HoldingButtonLayoutListener {

    void onBeforeExpand();

    void onExpand();

    void onBeforeCollapse();

    void onCollapse();

    void onOffsetChanged(float offset, boolean isCancel);
}
