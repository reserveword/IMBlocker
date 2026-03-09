package io.github.reserveword.imblocker.common.gui;

import java.util.Objects;

public class MultilineCursorInfo extends CursorInfo {
	public int cursorLineIndex;
	public double scrollY;

	public MultilineCursorInfo(int cursorLineIndex, double scrollY, 
			int cursorLineBeginIndex, int cursor, String text) {
		super(cursorLineBeginIndex, cursor, text);
		this.cursorLineIndex = cursorLineIndex;
		this.scrollY = scrollY;
	}
	
	public boolean updateCursorInfo(int cursorLineIndex, double scrollY, 
			int cursorLineBeginIndex, int cursor, String text) {
		boolean changed = false;
		if(this.cursorLineIndex != cursorLineIndex) {
			this.cursorLineIndex = cursorLineIndex;
			changed = true;
		}
		if(this.scrollY != scrollY) {
			this.scrollY = scrollY;
			changed = true;
		}
		if(this.cursorLineBeginIndex != cursorLineBeginIndex) {
			this.cursorLineBeginIndex = cursorLineBeginIndex;
			changed = true;
		}
		if(this.cursor != cursor) {
			this.cursor = cursor;
			changed = true;
		}
		if(!Objects.equals(this.text, text)) {
			this.text = text;
			changed = true;
		}
		return changed;
	}
}
