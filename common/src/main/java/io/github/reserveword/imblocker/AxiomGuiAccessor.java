package io.github.reserveword.imblocker;

public abstract class AxiomGuiAccessor {
	public static AxiomGuiAccessor instance;
	
	public abstract boolean isCaptureKeyboard();
	public abstract boolean isTextFieldFocused();
}
