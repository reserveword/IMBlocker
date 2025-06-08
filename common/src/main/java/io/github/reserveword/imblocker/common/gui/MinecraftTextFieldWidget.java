package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

public interface MinecraftTextFieldWidget extends MinecraftFocusableWidget {
	
	@Override
	default boolean getPreferredState() {
		return true;
	}
	
	default Point getCaretPos() {
		SinglelineCursorInfo cursorInfo = getCursorInfo();
		if(cursorInfo == null) {
			return MinecraftFocusableWidget.super.getCaretPos();
		}
		int caretX = (cursorInfo.hasBorder ? getPaddingX() : 0) + MinecraftClientAccessor.INSTANCE.getStringWidth(
				StringUtil.getSubstring(cursorInfo.text, cursorInfo.cursorLineBeginIndex, cursorInfo.cursor));
    	return new Point(getGuiScale(), caretX, cursorInfo.hasBorder ? (cursorInfo.widgetHeight - getFontHeight()) / 2 : 0);
	}

	SinglelineCursorInfo getCursorInfo();
	
	default int getPaddingX() {
		return 4;
	}
	
	default int getFontHeight() {
		return 8;
	}
}
