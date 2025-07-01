package io.github.reserveword.imblocker.common.gui;

public interface FocusableWidget extends FocusableObject {
	
	FocusContainer getFocusContainer();
	
	default double getGuiScale() {
		return getFocusContainer().getGuiScale();
	}
	
	default boolean isRenderable() {
		return true;
	}
}
