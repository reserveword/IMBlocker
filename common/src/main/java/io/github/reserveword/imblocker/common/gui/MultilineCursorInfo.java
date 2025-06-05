package io.github.reserveword.imblocker.common.gui;

public class MultilineCursorInfo extends CursorInfo {
	
	public final int cursorLineIndex;
	public final double scrollY;

	public MultilineCursorInfo(int cursorLineIndex, double scrollY, 
			int cursorLineBeginIndex, int cursor, String text) {
		super(cursorLineBeginIndex, cursor, text);
		this.cursorLineIndex = cursorLineIndex;
		this.scrollY = scrollY;
	}
}
