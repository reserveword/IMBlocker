package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMBlockerConfig;

public class CurrentScreenInfoOverlay {
	public static void renderScreenClassName(MinecraftRenderApi graphics) {
		if(IMBlockerConfig.INSTANCE.isScreenRecoveringEnabled()) {
			Object currentScreen = MinecraftScreenMonitor.getCurrentScreen();
			if(currentScreen != null) {
				graphics.drawText(currentScreen.getClass().getName(), 2, 2, -1);
			}
		}
	}
}
