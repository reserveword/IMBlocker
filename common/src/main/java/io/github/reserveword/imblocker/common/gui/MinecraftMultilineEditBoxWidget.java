package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

public interface MinecraftMultilineEditBoxWidget extends MinecraftFocusableWidget {
	
	default Point getCaretPos() {
		MultilineCursorInfo cursorInfo = getCursorInfo();
		int caretX = 4 + MinecraftClientAccessor.INSTANCE.getStringWidth(
				StringUtil.getSubstring(cursorInfo.text, cursorInfo.cursorLineBeginIndex, cursorInfo.cursor));
		int caretY = (int) (4 + cursorInfo.cursorLineIndex * 9 - cursorInfo.scrollY);
		return new Point(getGuiScale(), caretX, caretY);
	}
	
	default void imblocker$onCursorChanged() {
		if(updateCursorInfo() && isTrulyFocused()) {
			IMManager.updateCompositionWindowPos();
		}
	}
	
	default boolean updateCursorInfo() {
		return true;
	}
	
	MultilineCursorInfo getCursorInfo();
}
