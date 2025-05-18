package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.MinecraftClientAccessor;

public interface MinecraftMultilineEditBoxWidget {
	
	default Point getCaretPos(CursorInfo cursorInfo) {
		int lineY = (int) (4 + cursorInfo.cursorLineIndex * 9 - cursorInfo.scrollY);
		
		int caretX = 4, caretY;
		try {
			caretX += MinecraftClientAccessor.instance.getStringWidth(
					cursorInfo.text.substring(cursorInfo.cursorLineBeginIndex, cursorInfo.cursor));
		} catch (Exception e) {}
		if(lineY < 0) {
			caretY = 0;
		}else if(lineY > cursorInfo.widgetHeight) {
			caretY = cursorInfo.widgetHeight - 4;
		}else {
			caretY = lineY;
		}
		return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, caretY);
	}
}
