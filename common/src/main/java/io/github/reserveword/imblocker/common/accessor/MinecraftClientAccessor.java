package io.github.reserveword.imblocker.common.accessor;

import io.github.reserveword.imblocker.common.gui.Rectangle;

public abstract class MinecraftClientAccessor {
	
	public static MinecraftClientAccessor INSTANCE;
	
	public abstract void sendSafeKeyForFocusTracking(int key, int scancode);
	public abstract void execute(Runnable runnable);
	public abstract Rectangle getWindowBounds();
	public abstract Object getCurrentScreen();
	public abstract int getStringWidth(String text);
	public abstract void registerClientTickEvent(Runnable tickEvent);
}
