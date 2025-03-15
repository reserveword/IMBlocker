package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMManager;

public interface FocusableWidget {
	
	FocusContainer getFocusContainer();
	
	default void deliverFocus() {
		FocusManager.setFocusOwner(this);
		updateIMState();
	}
	
	default void lostFocus() {
		FocusManager.setFocusOwner(null);
	}
	
	default boolean isTrulyFocused() {
		return FocusManager.getFocusOwner() == this;
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
	
	boolean isWidgetEditable();
	
	default boolean getPreferredEnglishState() {
		return false;
	}
}
