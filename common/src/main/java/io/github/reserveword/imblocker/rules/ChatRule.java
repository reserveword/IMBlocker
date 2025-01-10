package io.github.reserveword.imblocker.rules;

import io.github.reserveword.imblocker.common.IMManager;

public class ChatRule implements Rule {
    public static boolean isChatScreenShowing = false;

    @Override
    public double Priority() {
        return 10;
    }

    @Override
    public boolean apply() {
        if (!isChatScreenShowing) {
            return false;
        } else if (FocusRule.focusedInputWidget.getText().trim().startsWith("/")) {
            IMManager.setState(true);
            IMManager.setEnglish(true);
            return true;
        } else {
            IMManager.setState(true);
            IMManager.setEnglish(false);
            return true;
        }
    }
}
