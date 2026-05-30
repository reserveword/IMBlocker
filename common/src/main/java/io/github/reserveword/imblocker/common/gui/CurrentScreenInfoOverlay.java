package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

public class CurrentScreenInfoOverlay {
	public static void renderScreenClassName(MinecraftRenderApi graphics) {
		if(IMBlockerConfig.INSTANCE.isScreenRecoveringEnabled()) {
			Object currentScreen = MinecraftClientAccessor.INSTANCE.getCurrentScreen();
			if(currentScreen != null) {
				graphics.drawText(currentScreen.getClass().getName(), 2, 2, -1);
			}
		}
	}
}
