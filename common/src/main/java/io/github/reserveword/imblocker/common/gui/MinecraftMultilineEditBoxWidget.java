package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

public interface MinecraftMultilineEditBoxWidget extends MinecraftAbstractTextInputWidget<MultilineCursorInfo> {
	
	default Point getCaretPos() {
		MultilineCursorInfo cursorInfo = getCursorInfo();
		int caretX = imblocker$getContentOffsetX() + MinecraftClientAccessor.INSTANCE.getStringWidth(
				StringUtil.getSubstring(cursorInfo.text, cursorInfo.cursorLineBeginIndex, cursorInfo.cursor));
		int caretY = (int) (imblocker$getContentOffsetY() + cursorInfo.cursorLineIndex * 9 - cursorInfo.scrollY);
		return new Point(getGuiScale(), caretX, caretY);
	}
	
	default int imblocker$getContentOffsetX() {
		return 4;
	}
	
	default int imblocker$getContentOffsetY() {
		return 4;
	}
}
