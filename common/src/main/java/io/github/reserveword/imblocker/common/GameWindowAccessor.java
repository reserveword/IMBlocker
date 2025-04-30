package io.github.reserveword.imblocker.common;

import io.github.reserveword.imblocker.common.gui.Rectangle;

public abstract class GameWindowAccessor {
	
	public static GameWindowAccessor instance;
	
	public abstract Rectangle getBounds();
}
