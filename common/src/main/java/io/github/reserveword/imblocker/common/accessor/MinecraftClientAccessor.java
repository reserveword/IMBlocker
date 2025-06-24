package io.github.reserveword.imblocker.common.accessor;

import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.gui.Rectangle;

public abstract class MinecraftClientAccessor {
	
	public static MinecraftClientAccessor INSTANCE;
	
	public void locateRealFocus() {
		Common.isTrackingFocus = true;
		sendSafeCharForFocusTracking();
		Common.isTrackingFocus = false;
	}
	
	public abstract void sendSafeCharForFocusTracking();
	public abstract void execute(Runnable runnable);
	public abstract Rectangle getWindowBounds();
	public abstract int getStringWidth(String text);
}
