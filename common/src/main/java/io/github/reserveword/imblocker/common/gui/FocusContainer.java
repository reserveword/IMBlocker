package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMManager;

public enum FocusContainer {
	MINECRAFT(true), IMGUI(false);
	
	private boolean isFocused;
	private FocusableWidget focusedWidget;
	
	private FocusContainer(boolean defaultFocusState) {
		isFocused = defaultFocusState;
	}
	
	public FocusableWidget getFocusedWidget() {
		return focusedWidget;
	}
	
	public void requestFocus(FocusableWidget toFocus) {
		if(focusedWidget != toFocus) {
			if(focusedWidget != null) {
				focusedWidget.lostFocus();
			}
			
			focusedWidget = toFocus;
			
			if(isFocused) {
				focusedWidget.deliverFocus();
			}
		}
	}
	
	public void removeFocus(FocusableWidget toRemove) {
		if(focusedWidget == toRemove) {
			toRemove.lostFocus();
			focusedWidget = null;
			if(isFocused) {
				IMManager.setState(false);
			}
		}
	}
	
	public void cancelFocus() {
		if(focusedWidget != null) {
			removeFocus(focusedWidget);
		}
	}
	
	public void deliverFocus() {
		isFocused = true;
		if(focusedWidget != null) {
			focusedWidget.deliverFocus();
		}else {
			IMManager.setState(false);
		}
	}
	
	public void lostFocus() {
		isFocused = false;
		if(focusedWidget != null) {
			focusedWidget.lostFocus();
		}
	}
}
