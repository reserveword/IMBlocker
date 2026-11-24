package io.github.reserveword.imblocker.common.gui.mc;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.FocusableObject;
import net.minecraft.client.gui.screens.Screen;

public abstract class MinecraftScreenMonitor {
	private static final boolean IS_COMPOSE_POSSIBLE =
			IMBlockerCore.hasMod("polycompose") || IMBlockerCore.hasMod("oneconfig");
	
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
		// Compose screens (OneConfig etc.) get their IME exclusively through the
		// PolyCompose session hook: the compose widget is the only focus owner
		// that should ever enable the IME there. Keeping the container path
		// (screen whitelist) enabled for them shows the candidate window at the
		// OS default position whenever the session has not started yet.
		boolean preferred = config.isScreenInWhitelist(screen) && !isComposeScreen(screen);
		FocusContainer.MINECRAFT.setPreferredState(preferred);
		isCharSimulationPreferred = config.isCharSimulationPreferred(screen);
		
		if(screen instanceof MinecraftFocusableWidget _screen) {
			_screen.imblocker$onFocusGained();
		}else {
			// A screen switch can drop the previous focus owner without a proper
			// focus transition (e.g. owner already cleared), which leaves the OS
			// IME in its stale state - often ON - so the candidate window shows
			// up at the OS default position until a real text field takes focus.
			// Sync the IM state to the new screen's preference in that case.
			FocusableObject owner = FocusManager.getFocusOwner();
			if(owner == null || owner == FocusContainer.MINECRAFT) {
				FocusContainer.MINECRAFT.updateIMState();
			}
		}
	}
	
	public static Screen getCurrentScreen() {
		return currentScreen;
	}
	
	public static boolean isCharSimulationPreferred() {
		return isCharSimulationPreferred;
	}
	
	private static boolean isComposeScreen(Screen screen) {
		if(!IS_COMPOSE_POSSIBLE || screen == null) {
			return false;
		}
		for(Class<?> cls = screen.getClass(); cls != null && cls != Object.class; cls = cls.getSuperclass()) {
			if("org.polyfrost.compose.minecraft.screen.ComposeScreen".equals(cls.getName())) {
				return true;
			}
		}
		return false;
	}
}
