package io.github.reserveword.imblocker.common;

import io.github.reserveword.imblocker.common.gui.Rectangle;

public abstract class MinecraftClientAccessor {
	
	public static MinecraftClientAccessor instance;
	
	public abstract void execute(Runnable runnable);
	public abstract Rectangle getWindowBounds();
	public abstract int getStringWidth(String text);
}
