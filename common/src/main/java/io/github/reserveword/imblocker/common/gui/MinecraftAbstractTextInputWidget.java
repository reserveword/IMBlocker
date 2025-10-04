package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMManager;

public interface MinecraftAbstractTextInputWidget<CI extends CursorInfo> extends MinecraftFocusableWidget {
	
	@Override
	default void deliverFocus() {
		updateCursorInfo();
		MinecraftFocusableWidget.super.deliverFocus();
	}
	
	default void imblocker$onCursorChanged() {
		if(isTrulyFocused() && updateCursorInfo()) {
			IMManager.updateCompositionWindowPos();
		}
	}
	
	default boolean updateCursorInfo() {
		return true;
	}

	CI getCursorInfo();
}
