package io.github.reserveword.imblocker.common;

import com.sun.jna.Platform;

public final class IMManager {
	private static final PlatformIMManager INSTANCE;
	
	public sealed interface PlatformIMManager permits IMManagerWindows, IMManagerMac, IMManagerStub {
		
		void setState(boolean on);
		
		void setImmOnState(boolean isEN);
		
		void syncState();
		
		boolean getState();
	}
	
	private IMManager() {}
	
	public static void setState(boolean on) {
		INSTANCE.setState(on);
	}
	
	public static void setImmOnState(boolean isEN) {
		INSTANCE.setImmOnState(isEN);
	}
	
	public static void syncState() {
		INSTANCE.syncState();
	}
	
	public static boolean getState() {
		return INSTANCE.getState();
	}
	
	static {
		if(Platform.isWindows()) {
			INSTANCE = new IMManagerWindows();
		}else if(Platform.isMac()) {
			INSTANCE = new IMManagerMac();
		}else {
			Common.LOGGER.warn("Unsupported platform, using stub");
			INSTANCE = new IMManagerStub();
		}
	}
}
