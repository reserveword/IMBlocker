package io.github.reserveword.imblocker.common.accessor;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.Rectangle;

public abstract class MinecraftClientAccessor {
	
	public static MinecraftClientAccessor INSTANCE;
	
	public void locateRealFocus() {
		IMBlockerCore.invokeLater(() ->{
			IMBlockerCore.isTrackingFocus = true;
			try {
				sendSafeCharForFocusTracking();
			} catch (Throwable e) {
				IMBlockerCore.LOGGER.warn("failed to locate focus with char simulation");
			}
			if(!IMBlockerCore.isFocusLocated) {
				FocusContainer.MINECRAFT.cancelFocus();
			}
			IMBlockerCore.isTrackingFocus = false;
			IMBlockerCore.isFocusLocated = false;
		});
	}
	
	public abstract void sendSafeCharForFocusTracking();
	public abstract void execute(Runnable runnable);
	public abstract Rectangle getWindowBounds();
	public abstract int getStringWidth(String text);
}
