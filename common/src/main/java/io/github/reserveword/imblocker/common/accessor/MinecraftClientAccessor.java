package io.github.reserveword.imblocker.common.accessor;

import io.github.reserveword.imblocker.common.gui.Dimension;
import io.github.reserveword.imblocker.common.gui.Rectangle;

public abstract class MinecraftClientAccessor {
	
	public static MinecraftClientAccessor INSTANCE;
	
	public abstract void sendSafeCharForFocusTracking(int codePoint);
	public abstract void execute(Runnable runnable);
	public abstract Rectangle getWindowBounds();
	public abstract Dimension getContentSize();
	public abstract Object getCurrentScreen();
	public abstract int getStringWidth(String text);
	public abstract void registerClientTickEvent(Runnable tickEvent);
}
