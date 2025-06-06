package io.github.reserveword.imblocker.common;

import com.sun.jna.Platform;

public final class IMManager {
	private static final PlatformIMManager INSTANCE;
	
	public interface PlatformIMManager {
		
		void setState(boolean on);
		
		void setEnglishState(boolean isEN);
		
		default void updateCompositionWindowPos() {}
	}
	
	private IMManager() {}
	
	public static void setState(boolean on) {
		INSTANCE.setState(on);
	}
	
	public static void setEnglishState(boolean isEN) {
		if(!IMBlockerConfig.INSTANCE.isCompatibilityModeEnabled()) {
			INSTANCE.setEnglishState(isEN);
		}
	}
	
	public static void updateCompositionWindowPos() {
		if(!IMBlockerConfig.INSTANCE.isCompatibilityModeEnabled()) {
			INSTANCE.updateCompositionWindowPos();
		}
	}
	
	static {
		if(Platform.isWindows()) {
			INSTANCE = new IMManagerWindows();
		}else if(Platform.isMac()) {
			INSTANCE = new IMManagerMac();
		}else if(Platform.isLinux()) {
			INSTANCE = new IMManagerLinux();
		}else {
			Common.LOGGER.warn("Unsupported platform, using stub");
			INSTANCE = new IMManagerStub();
		}
	}
}
