package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.MinecraftClientAccessor;

public interface MinecraftTextFieldWidget {
	
	default Point getCaretPosImpl() {
		CursorInfo cursorInfo = getCursorInfo();
		int caretX = (cursorInfo.hasBorder ? 4 : 0);
    	try {
        	caretX += MinecraftClientAccessor.instance.getStringWidth(
        			cursorInfo.text.substring(cursorInfo.cursorLineBeginIndex, cursorInfo.cursor));
		} catch (Exception e) {}
    	return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, (cursorInfo.widgetHeight - 8) / 2);
	}
	
	CursorInfo getCursorInfo();
}
