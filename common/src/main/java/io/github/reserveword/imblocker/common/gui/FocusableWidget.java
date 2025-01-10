package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMManager;

public interface FocusableWidget {
	
	FocusContainer getFocusContainer();
	
	default void deliverFocus() {
		setTrulyFocused(true);
		updateIMState();
	}
	
	default void lostFocus() {
		setTrulyFocused(false);
	}
	
	default void setTrulyFocused(boolean isTrulyFocused) {}
	
	default void updateIMState() {
		IMManager.setState(isWidgetEditable());
		if(isWidgetEditable()) {
			IMManager.setEnglishState(getPreferredEnglishState());
		}
	}
	
	default void updateEnglishState() {
		if(isWidgetEditable()) {
			IMManager.setEnglishState(getPreferredEnglishState());
		}
	}
	
	public abstract boolean isWidgetEditable();
	
	default boolean getPreferredEnglishState() {
		return false;
	}
}
