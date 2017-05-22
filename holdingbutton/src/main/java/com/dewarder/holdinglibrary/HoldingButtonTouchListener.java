package com.dewarder.holdinglibrary;

/**
 * Interface is used to intercept touch events.
 * Can be used for cases when permissions or something else are needed and expanding animation isn't needed
 */
public interface HoldingButtonTouchListener {

    /**
     * Indicates that holding view is touched.
     * Method is called before any method of `HoldingButtonLayoutListener`.
     * @return false - if expanding animation must be canceled, true - if animation must be proceeded
     */
    boolean onHoldingViewTouched();
}
