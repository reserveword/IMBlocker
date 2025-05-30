package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

public interface MinecraftMultilineEditBoxWidget {
	
	default Point getCaretPos(CursorInfo cursorInfo) {
		int lineY = (int) (4 + cursorInfo.cursorLineIndex * 9 - cursorInfo.scrollY);
		int caretX = 4 + MinecraftClientAccessor.INSTANCE.getStringWidth(
				StringUtil.getSubstring(cursorInfo.text, cursorInfo.cursorLineBeginIndex, cursorInfo.cursor));
		int caretY = MathHelper.clamp(lineY, 0, cursorInfo.widgetHeight - 4);
		return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, caretY);
	}
}
