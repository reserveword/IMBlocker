package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import net.minecraft.client.gui.screens.Screen;

public abstract class MinecraftScreenMonitor {
	private static Screen currentScreen;
	private static boolean isCharSimulationPreferred;
	
	public static void onScreenChanged(Screen screen) {
		currentScreen = screen;
		
		if(!IMBlockerCore.isFTBScreen(screen)) {
			FocusContainer.MINECRAFT.clearFocus();
		}
		
		IMBlockerConfig config = IMBlockerConfig.INSTANCE;
		if(config.isScreenRecoveringEnabled() && screen != null) {
			config.recoverScreen(screen.getClass().getName());
		}
		FocusContainer.MINECRAFT.setPreferredState(config.isScreenInWhitelist(screen));
		isCharSimulationPreferred = config.isCharSimulationPreferred(screen);
		
		if(screen instanceof MinecraftFocusableWidget _screen) {
			_screen.imblocker$onFocusGained();
		}
	}
	
	public static Screen getCurrentScreen() {
		return currentScreen;
	}
	
	public static boolean isCharSimulationPreferred() {
		return isCharSimulationPreferred;
	}
}
