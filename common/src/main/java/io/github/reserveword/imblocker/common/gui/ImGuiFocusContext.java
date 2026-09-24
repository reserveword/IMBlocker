package io.github.reserveword.imblocker.common.gui;

public final class ImGuiFocusContext extends FocusContainer {

	ImGuiFocusContext() {
		super(false);
	}
	
	@Override
	public int getFontHeight() {
		return 24;
	}
	
	@Override
	public boolean getPreferredEnglishState() {
		return true;
	}
}
