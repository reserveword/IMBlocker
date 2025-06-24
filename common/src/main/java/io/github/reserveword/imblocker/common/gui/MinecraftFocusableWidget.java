package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

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
		if(!IMBlockerConfig.INSTANCE.isTwoFactorFocusTrackingEnabled()) {
			getFocusContainer().requestFocus(this);
		}else {
			MinecraftClientAccessor.INSTANCE.locateRealFocus();
		}
	}
	 
	default void onMinecraftWidgetFocusLost() {
		if(!IMBlockerConfig.INSTANCE.isTwoFactorFocusTrackingEnabled()) {
			getFocusContainer().removeFocus(this);
		}else {
			MinecraftClientAccessor.INSTANCE.locateRealFocus();
		}
	}
	
	@Override
	default boolean getPreferredState() {
		return false;
	}
}
