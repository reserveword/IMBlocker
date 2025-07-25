package io.github.reserveword.imblocker.common.gui;

public interface MinecraftFocusableWidget extends FocusableWidget {
	
	default FocusContainer getFocusContainer() {
		return FocusContainer.MINECRAFT;
	}
	
	default void imblocker$onFocusChanged(boolean isFocused) {
		if(isFocused) {
			imblocker$onFocusGained();
		}else {
			imblocker$onFocusLost();
		}
	}
	
	default void imblocker$onFocusGained() {
		getFocusContainer().requestFocus(this);
	}
	
	default void imblocker$onFocusLost() {
		getFocusContainer().removeFocus(this);
	}
}
