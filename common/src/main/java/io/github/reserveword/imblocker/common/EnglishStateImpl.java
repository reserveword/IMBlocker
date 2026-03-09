package io.github.reserveword.imblocker.common;

import java.util.function.Consumer;

import io.github.reserveword.imblocker.common.gui.FocusableObject;

public enum EnglishStateImpl {
	CONVERSION_STATUS(
			focusOwner -> {
				boolean shouldEnableIME = focusOwner.getPreferredState();
				IMManager.setState(shouldEnableIME);
				if(shouldEnableIME) {
					IMManager.setEnglishState(focusOwner.getPreferredEnglishState());
				}
			},
			focusOwner -> {
				IMManager.setEnglishState(focusOwner.getPreferredEnglishState());
			}),
	DISABLE_IM(
			focusOwner -> {
				IMManager.setState(focusOwner.getPreferredState() && !focusOwner.getPreferredEnglishState());
			},
			focusOwner -> {
				IMManager.setState(!focusOwner.getPreferredEnglishState());
			});
	
	private final Consumer<FocusableObject> updateIMState;
	private final Consumer<FocusableObject> updateEnglishState;
	
	private EnglishStateImpl(Consumer<FocusableObject> updateIMState, 
			Consumer<FocusableObject> updateEnglishState) {
		this.updateIMState = updateIMState;
		this.updateEnglishState = updateEnglishState;
	}
	
	public void updateIMState(FocusableObject focusOwner) {
		updateIMState.accept(focusOwner);
	}
	
	public void updateEnglishState(FocusableObject focusOwner) {
		updateEnglishState.accept(focusOwner);
	}
}
