package io.github.reserveword.imblocker.common.gui;

public class GenericWhitelistScreen implements MinecraftFocusableWidget {
	
	private static final GenericWhitelistScreen INSTANCE = new GenericWhitelistScreen();
	
	@Override
	public boolean isWidgetEditable() {
		return true;
	}
	
	public static GenericWhitelistScreen getInstance() {
		return INSTANCE;
	}
}
