package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.MinecraftClientAccessor;

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
		IMManager.setState(getPreferredState());
		if(getPreferredState()) {
			IMManager.setEnglishState(getPreferredEnglishState());
			IMManager.updateCompositionWindowPos();
		}
	}
	
	default void updateEnglishState() {
		if(getPreferredState()) {
			IMManager.setEnglishState(getPreferredEnglishState());
		}
	}
	
	boolean isWidgetEditable();
	
	default boolean getPreferredState() {
		return isWidgetEditable();
	}
	
	default boolean getPreferredEnglishState() {
		return false;
	}
	
	default Rectangle getBoundsAbs() {
		Rectangle bounds = MinecraftClientAccessor.instance.getWindowBounds();
		return new Rectangle(bounds.width() / 3, bounds.height() / 2, 0, 0);
	}
	
	default Point getCaretPos() {
		return Point.TOP_LEFT;
	}
}
