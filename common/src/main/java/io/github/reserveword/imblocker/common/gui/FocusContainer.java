package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;

public enum FocusContainer {
	MINECRAFT(true, 1.0), IMGUI(false, 4.0);
	
	private double guiScaleFactor;
	
	private boolean isFocused;
	private FocusableWidget focusedWidget;
	
	private FocusContainer(boolean defaultFocusState, double guiScale) {
		isFocused = defaultFocusState;
		guiScaleFactor = guiScale;
	}
	
	public FocusableWidget getFocusedWidget() {
		return focusedWidget;
	}
	
	public void requestFocus(FocusableWidget toFocus) {
		if(IMBlockerCore.isTrackingFocus) {
			IMBlockerCore.isFocusLocated = true;
		}
		
		if(focusedWidget != toFocus) {
			if(isFocused) {
				if(focusedWidget != null) {
					focusedWidget.lostFocus();
				}
				focusedWidget = toFocus;
				focusedWidget.deliverFocus();
			}else {
				focusedWidget = toFocus;
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
	
	public void setGuiScaleFactor(double factor) {
		this.guiScaleFactor = factor;
	}
	
	public double getGuiScaleFactor() {
		return guiScaleFactor;
	}
}
