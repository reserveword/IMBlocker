package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMManager;

public interface FocusableWidget {
	
	FocusableWidget[] focusedWidget = new FocusableWidget[1];
	
	FocusContainer getFocusContainer();
	
	default void deliverFocus() {
		focusedWidget[0] = this;
		updateIMState();
	}
	
	default void lostFocus() {
		focusedWidget[0] = null;
	}
	
	default boolean isTrulyFocused() {
		return focusedWidget[0] == this;
	}
	
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
