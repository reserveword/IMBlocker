package io.github.reserveword.imblocker.common.gui;

public enum FocusContainer {
	MINECRAFT, IMGUI;
	
	private FocusableWidget focusedWidget;
	
	public FocusableWidget getFocusedWidget() {
		return focusedWidget;
	}
	
	public void requestFocus(FocusableWidget toFocus) {
		if(this.focusedWidget != toFocus) {
			this.focusedWidget = toFocus;
			FocusManager.requestUpdateIMState(this);
		}
	}
	
	public void removeFocus(FocusableWidget toCancel) {
		if(this.focusedWidget == toCancel) {
			this.focusedWidget = null;
			FocusManager.requestUpdateIMState(this);
		}
	}
	
	public void cancelFocus() {
		removeFocus(focusedWidget);
	}
	
	public void requestUpdateIMState(FocusableWidget requestWidget) {
		if(focusedWidget == requestWidget) {
			FocusManager.requestUpdateIMState(this);
		}
	}
	
	public boolean getPreferredIMState() {
		return focusedWidget != null ? focusedWidget.isWidgetEditable() : false;
	}
	
	public void requestUpdateEnglishState(FocusableWidget requestWidget) {
		if(focusedWidget == requestWidget) {
			FocusManager.requestUpdateEnglishState(this);
		}
	}
	
	public boolean getPreferredEnglishState() {
		return focusedWidget != null ? focusedWidget.getPreferredEnglishState() : true;
	}
}
