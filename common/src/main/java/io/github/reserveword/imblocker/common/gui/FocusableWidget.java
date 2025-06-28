package io.github.reserveword.imblocker.common.gui;

public interface FocusableWidget extends FocusableObject {
	
	FocusContainer getFocusContainer();
	
	@Override
	default Rectangle getBoundsAbs() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	default Point getCaretPos() {
		throw new UnsupportedOperationException();
	}
	
	default double getGuiScale() {
		return getFocusContainer().getGuiScale();
	}
}
