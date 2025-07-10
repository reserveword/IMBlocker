package io.github.reserveword.imblocker.common.gui;

public interface MinecraftFocusableWidget extends FocusableWidget {
	
	default FocusContainer getFocusContainer() {
		return FocusContainer.MINECRAFT;
	}
	
	default void onMinecraftWidgetFocusChanged(boolean isFocused) {
		if(isFocused) {
			onMinecraftWidgetFocusGained();
		}else {
			onMinecraftWidgetFocusLost();
		}
	}
	
	default void onMinecraftWidgetFocusGained() {
		getFocusContainer().requestFocus(this);
	}
	
	default void onMinecraftWidgetFocusLost() {
		getFocusContainer().removeFocus(this);
	}
}
