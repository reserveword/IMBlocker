package io.github.reserveword.imblocker.common.accessor;

import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.gui.Dimension;
import io.github.reserveword.imblocker.common.gui.Rectangle;

public abstract class MinecraftClientAccessor {
	
	public static final MinecraftClientAccessor INSTANCE;
	
	public abstract void sendSafeCharForFocusTracking(int codePoint);
	public abstract void execute(Runnable runnable);
	public abstract Rectangle getWindowBounds();
	public abstract Dimension getContentSize();
	public abstract Object getCurrentScreen();
	public abstract int getStringWidth(String text);
	
	static {
		Class<?> minecraftClientAccessorCls = null;
		try {
			minecraftClientAccessorCls = Class.forName("io.github.reserveword.imblocker.MinecraftClientAccessorImpl");
		} catch (Exception e) {
			e.printStackTrace();
		}
		INSTANCE = (MinecraftClientAccessor) ReflectionUtil.newInstance(minecraftClientAccessorCls, new Class[0]);
	}
}
