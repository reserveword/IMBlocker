package io.github.reserveword.imblocker.rules;

import io.github.reserveword.imblocker.common.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.common.IMManager;

public class FocusRule implements Rule {
    public static FocusableWidgetAccessor focusedInputWidget = null;

    public static void focusChanged(FocusableWidgetAccessor widget, boolean isFocused) {
        if (isFocused) {
            focusGained(widget);
        } else {
            focusLost(widget);
        }
    }

    public static void focusGained(FocusableWidgetAccessor widget) {
        focusedInputWidget = widget;
        if (focusedInputWidget.isWidgetEditable()) {
            IMManager.setState(true);
        }
    }

    public static void focusLost(FocusableWidgetAccessor widget) {
        if (focusedInputWidget == widget) {
            focusedInputWidget = null;
            IMManager.setState(false);
        }
    }

    @Override
    public double Priority() {
        return 0;
    }

    @Override
    public boolean apply() {
        if (focusedInputWidget == null || !focusedInputWidget.isWidgetEditable()) {
            return false;
        }
        IMManager.setState(true);
        return true;
    }
}
