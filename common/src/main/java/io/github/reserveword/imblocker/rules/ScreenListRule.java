package io.github.reserveword.imblocker.rules;

import io.github.reserveword.imblocker.common.IMManager;

public class ScreenListRule implements Rule {
    public static boolean isWhiteListScreenShowing = false;

    @Override
    public double Priority() {
        return 100;
    }

    @Override
    public boolean apply() {
        if (isWhiteListScreenShowing) {
            IMManager.setState(true);
            return true;
        }
        return false;
    }
}
