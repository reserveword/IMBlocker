package io.github.reserveword.imblocker.common.gui;

public class SinglelineCursorInfo extends CursorInfo {
	
	public final boolean hasBorder;
	public final int widgetHeight;
	
	public SinglelineCursorInfo(boolean hasBorder, int widgetHeight, 
			int cursorLineBeginIndex, int cursor, String text) {
		super(cursorLineBeginIndex, cursor, text);
		this.hasBorder = hasBorder;
		this.widgetHeight = widgetHeight;
	}
}
