package io.github.reserveword.imblocker.common.gui;

import java.util.Objects;

public class SinglelineCursorInfo extends CursorInfo {
	public boolean hasBorder;
	public int widgetHeight;
	
	public SinglelineCursorInfo(boolean hasBorder, int widgetHeight, 
			int cursorLineBeginIndex, int cursor, String text) {
		super(cursorLineBeginIndex, cursor, text);
		this.hasBorder = hasBorder;
		this.widgetHeight = widgetHeight;
	}
	
	public boolean updateCursorInfo(boolean hasBorder, int widgetHeight, 
			int cursorLineBeginIndex, int cursor, String text) {
		//There is already a mod that triggers text changed callback 8k times per second BUT DO NOTHING 
		//thus these checks are necessary.
		boolean changed = false;
		if(this.hasBorder != hasBorder) {
			this.hasBorder = hasBorder;
			changed = true;
		}
		if(this.widgetHeight != widgetHeight) {
			this.widgetHeight = widgetHeight;
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
