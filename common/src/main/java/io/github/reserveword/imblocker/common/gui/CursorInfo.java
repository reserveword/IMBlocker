package io.github.reserveword.imblocker.common.gui;

public abstract class CursorInfo {

	public final int cursorLineBeginIndex;
	public final int cursor;
	public final String text;
	
	public CursorInfo(int cursorLineBeginIndex, int cursor, String text) {
		this.cursorLineBeginIndex = cursorLineBeginIndex;
		this.text = text;
		this.cursor = cursor;
	}
}
