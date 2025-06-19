package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

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
		boolean shouldEnableIME = getPreferredState();
		IMManager.setState(shouldEnableIME);
		if(shouldEnableIME) {
			IMManager.updateCompositionWindowPos();
			IMManager.updateCompositionFontSize();
			IMManager.setEnglishState(getPreferredEnglishState());
		}
	}
	
	default void updateEnglishState() {
		if(getPreferredState()) {
			IMManager.setEnglishState(getPreferredEnglishState());
		}
	}
	
	boolean getPreferredState();
	
	default boolean getPreferredEnglishState() {
		return false;
	}
	
	default Rectangle getBoundsAbs() {
		Rectangle bounds = MinecraftClientAccessor.INSTANCE.getWindowBounds();
		return new Rectangle(0, 0, bounds.width(), bounds.height());
	}
	
	default Point getCaretPos() {
		Rectangle bounds = MinecraftClientAccessor.INSTANCE.getWindowBounds();
		return new Point(bounds.width() / 3, bounds.height() / 2);
	}
	
	default double getGuiScale() {
		return getFocusContainer().getGuiScaleFactor();
	}
}
