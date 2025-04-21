package io.github.reserveword.imblocker.common.gui;

public interface MinecraftFocusableWidget extends FocusableWidget {
	
	default FocusContainer getFocusContainer() {
		return FocusContainer.MINECRAFT;
	}
	
	default void onFocusChanged(boolean isFocused) {
		if(isFocused) {
			onFocusGained();
		}else {
			onFocusLost();
		}
	}
	
	 default void onFocusGained() {
		 getFocusContainer().requestFocus(this);
	 }
	 
	 default void onFocusLost() {
		 getFocusContainer().removeFocus(this);
	 }
	 
	 default void setPreferredEditState(boolean state) {}
	 
	 default void setPreferredEnglishState(boolean state) {}
}
