package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

public interface MinecraftTextFieldWidget extends MinecraftFocusableWidget {
	
	@Override
	default void deliverFocus() {
		updateCursorInfo();
		MinecraftFocusableWidget.super.deliverFocus();
	}
	
	default void setPreferredEditState(boolean state) {}
	 
	default void setPreferredEnglishState(boolean state) {}
	
	default boolean getPrimaryEnglishState() {
		return IMBlockerConfig.INSTANCE.getPrimaryEnglishState().getBoolean();
	}
	
	default void imblocker$onBoundsChanged() {
		if(isTrulyFocused()) {
			IMManager.updateCompositionWindowPos();
		}
	}
	
	@Override
	default Rectangle getBoundsAbs() {
		return Rectangle.EMPTY;
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
	
	default void imblocker$onCursorChanged() {
		if(updateCursorInfo() && isTrulyFocused()) {
			IMManager.updateCompositionWindowPos();
		}
	}
	
	default boolean updateCursorInfo() {
		return true;
	}

	SinglelineCursorInfo getCursorInfo();
	
	default int getPaddingX() {
		return 4;
	}
	
	default void checkVisibility(long lastGameRenderTime) {}
}
