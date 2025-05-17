package io.github.reserveword.imblocker.common.gui;

public class CursorInfo {
	
	public final boolean hasBorder;
	public final int widgetHeight;
	public final int cursorLineIndex;
	public final double scrollY;
	public final int cursorLineBeginIndex;
	public final int cursor;
	public final String text;
	
	public CursorInfo(boolean hasBorder, int widgetHeight, int cursorLineIndex, 
			double scrollY, int cursorLineBeginIndex, int cursor, String text) {
		this.hasBorder = hasBorder;
		this.widgetHeight = widgetHeight;
		this.cursorLineIndex = cursorLineIndex;
		this.scrollY = scrollY;
		this.cursorLineBeginIndex = cursorLineBeginIndex;
		this.text = text;
		this.cursor = cursor;
	}
}
