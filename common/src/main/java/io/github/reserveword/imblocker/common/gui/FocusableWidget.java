package io.github.reserveword.imblocker.common.gui;

public interface FocusableWidget {
	
	FocusContainer getFocusContainer();
	
	public abstract boolean isWidgetEditable();
	
	default boolean getPreferredEnglishState() {
		return false;
	}
}
