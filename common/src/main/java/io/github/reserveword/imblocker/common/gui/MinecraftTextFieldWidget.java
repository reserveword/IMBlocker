package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

public interface MinecraftTextFieldWidget extends MinecraftAbstractTextInputWidget<SinglelineCursorInfo> {
	
	default void setPreferredEnglishState(boolean state) {}
	
	default boolean getPrimaryEnglishState() {
		return IMBlockerConfig.INSTANCE.getPrimaryEnglishState().getBoolean();
	}
	
	default Point getCaretPos() {
		SinglelineCursorInfo cursorInfo = getCursorInfo();
		if(cursorInfo == null) {
			return Point.TOP_LEFT;
		}
		int caretX = (cursorInfo.hasBorder ? getPaddingX() : 0) + MinecraftClientAccessor.INSTANCE.getStringWidth(
				StringUtil.getSubstring(cursorInfo.text, cursorInfo.cursorLineBeginIndex, cursorInfo.cursor));
		int caretY = cursorInfo.hasBorder ? (cursorInfo.widgetHeight - getFontHeight()) / 2 : 0;
    	return new Point(getGuiScale(), caretX, caretY);
	}
	
	default int getPaddingX() {
		return 4;
	}
	
	default void checkVisibility(long lastGameRenderTime) {}
}
