package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMManager;

public interface FocusableWidget {
	
	FocusContainer getFocusContainer();
	
	default void deliverFocus() {
		setTrulyFocused(true);
		IMManager.updateIMState(isWidgetEditable(), getPreferredEnglishState());
	}
	
	default void lostFocus() {
		setTrulyFocused(false);
	}
	
	default void setTrulyFocused(boolean isTrulyFocused) {}
	
	public abstract boolean isWidgetEditable();
	
	default boolean getPreferredEnglishState() {
		return false;
	}
}
