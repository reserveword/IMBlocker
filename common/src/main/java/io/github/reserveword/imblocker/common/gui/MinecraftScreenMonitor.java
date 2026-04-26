package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import net.minecraft.client.gui.screens.Screen;

public interface MinecraftScreenMonitor {
	public static void onScreenChanged(Screen screen) {
		if(IMBlockerConfig.INSTANCE.isScreenRecoveringEnabled() && screen != null) {
			IMBlockerConfig.INSTANCE.recoverScreen(screen.getClass().getName());
		}
		
		if(IMBlockerCore.isFTBScreen(screen)) {
			FocusContainer.MINECRAFT.clearFocus();
		}
		FocusContainer.MINECRAFT.setPreferredState(IMBlockerConfig.INSTANCE.isScreenInWhitelist(screen));
	}
}
