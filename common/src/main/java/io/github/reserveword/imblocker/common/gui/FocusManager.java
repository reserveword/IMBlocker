package io.github.reserveword.imblocker.common.gui;

public class FocusManager {
	
	private static boolean isWindowFocused = true;
	private static FocusContainer focusedContainer = FocusContainer.MINECRAFT;
	
	private static FocusableWidget focusOwner;
	
	public static void requestFocus(FocusContainer container) {
		if(focusedContainer != container) {
			if(isWindowFocused) {
				focusedContainer.lostFocus();
				container.deliverFocus();
			}
			
			focusedContainer = container;
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
	
	public static void setFocusOwner(FocusableWidget focusOwner) {
		FocusManager.focusOwner = focusOwner;
	}
	
	public static FocusableWidget getFocusOwner() {
		return focusOwner;
	}
}
