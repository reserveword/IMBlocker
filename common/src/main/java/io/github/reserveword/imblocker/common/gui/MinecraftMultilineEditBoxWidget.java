package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientUtil;

public interface MinecraftMultilineEditBoxWidget extends MinecraftAbstractTextInputWidget<MultilineCursorInfo> {
	
	default Point getCaretPos() {
		MultilineCursorInfo cursorInfo = getCursorInfo();
		int caretX = 4 + MinecraftClientUtil.getStringWidth(
				StringUtil.getSubstring(cursorInfo.text, cursorInfo.cursorLineBeginIndex, cursorInfo.cursor));
		int caretY = (int) (4 + cursorInfo.cursorLineIndex * 9 - cursorInfo.scrollY);
		return new Point(getGuiScale(), caretX, caretY);
	}
}
