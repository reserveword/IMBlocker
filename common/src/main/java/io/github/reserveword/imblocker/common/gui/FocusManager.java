package io.github.reserveword.imblocker.common.gui;

public class FocusManager {
	
	private static boolean isWindowFocused = true;
	private static FocusContainer focusedContainer = FocusContainer.MINECRAFT;
	
	public static void requestFocus(FocusContainer container) {
		if(focusedContainer != container) {
			focusedContainer.lostFocus();
		}
		
		focusedContainer = container;
		
		if(isWindowFocused) {
			focusedContainer.deliverFocus();
		}
	}
	
	public static void setWindowFocused(boolean windowFocused) {
		isWindowFocused = windowFocused;
		if(isWindowFocused) {
			focusedContainer.deliverFocus();
		}else {
			focusedContainer.lostFocus();
		}
	}
}
