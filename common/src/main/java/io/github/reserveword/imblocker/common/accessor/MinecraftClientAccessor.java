package io.github.reserveword.imblocker.common.accessor;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.GenericWhitelistScreen;
import io.github.reserveword.imblocker.common.gui.Rectangle;

public abstract class MinecraftClientAccessor {
	
	public static MinecraftClientAccessor INSTANCE;
	
	public void locateRealFocus() {
		IMBlockerCore.invokeLater(() -> {
			IMBlockerCore.isTrackingFocus = true;
			try {
				sendSafeCharForFocusTracking();
			} catch (Throwable e) {
				IMBlockerCore.LOGGER.warn("failed to locate focus with char simulation");
			}
			if(!IMBlockerCore.isFocusLocated) {
				if(IMBlockerConfig.INSTANCE.isScreenInWhitelist(getCurrentScreen())) {
					FocusContainer.MINECRAFT.requestFocus(GenericWhitelistScreen.getInstance());
				}else {
					FocusContainer.MINECRAFT.cancelFocus();
				}
			}
			IMBlockerCore.isTrackingFocus = false;
			IMBlockerCore.isFocusLocated = false;
		});
	}
	
	public abstract void sendSafeCharForFocusTracking();
	public abstract void execute(Runnable runnable);
	public abstract Rectangle getWindowBounds();
	public abstract Object getCurrentScreen();
	public abstract int getStringWidth(String text);
	public abstract void registerClientTickEvent(Runnable tickEvent);
}
