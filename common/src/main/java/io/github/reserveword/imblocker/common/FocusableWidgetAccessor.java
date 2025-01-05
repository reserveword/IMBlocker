package io.github.reserveword.imblocker.common;

public interface FocusableWidgetAccessor {
    boolean isWidgetEditable();

    default String getText() {
        return "";
    }
}
