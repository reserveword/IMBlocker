package io.github.reserveword.imblocker.common.gui;

public abstract class CursorInfo {
	public int cursorLineBeginIndex;
	public int cursor;
	public String text;
	
	public CursorInfo(int cursorLineBeginIndex, int cursor, String text) {
		this.cursorLineBeginIndex = cursorLineBeginIndex;
		this.text = text;
		this.cursor = cursor;
	}
}
