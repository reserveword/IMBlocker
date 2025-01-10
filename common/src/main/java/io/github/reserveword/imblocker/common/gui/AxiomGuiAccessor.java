package io.github.reserveword.imblocker.common.gui;

public abstract class AxiomGuiAccessor {
	public static AxiomGuiAccessor instance;
	
	public abstract boolean isCaptureKeyboard();
	public abstract boolean isTextFieldFocused();
}
