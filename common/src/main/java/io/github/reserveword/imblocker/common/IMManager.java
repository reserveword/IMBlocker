package io.github.reserveword.imblocker.common;

import com.sun.jna.Platform;

public final class IMManager {
	private static final PlatformIMManager INSTANCE;
	
	public sealed interface PlatformIMManager permits IMManagerWindows, IMManagerMac, IMManagerLinux, IMManagerStub {
		
		void setState(boolean on);
		
		void setEnglishState(boolean isEN);
	}
	
	private IMManager() {}
	
	public static void updateIMState(boolean state, boolean englishState) {
		INSTANCE.setState(state);
		if(state) {
			INSTANCE.setEnglishState(englishState);
		}
	}
	
	public static void setState(boolean on) {
		INSTANCE.setState(on);
	}
	
	public static void setEnglishState(boolean isEN) {
		INSTANCE.setEnglishState(isEN);
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
