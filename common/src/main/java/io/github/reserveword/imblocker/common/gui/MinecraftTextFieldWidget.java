package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.StringUtil;

public interface MinecraftTextFieldWidget {
	
	default Point getCaretPos(CursorInfo cursorInfo) {
		int caretX = (cursorInfo.hasBorder ? 4 : 0) + MinecraftClientAccessor.instance.getStringWidth(
				StringUtil.getSubstring(cursorInfo.text, cursorInfo.cursorLineBeginIndex, cursorInfo.cursor));
    	return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, (cursorInfo.widgetHeight - 8) / 2);
	}
}
