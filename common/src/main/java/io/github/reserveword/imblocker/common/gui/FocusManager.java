package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMManager;

public class FocusManager {
	
	private static boolean isWindowFocused = true;
	private static FocusContainer focusedContainer = FocusContainer.MINECRAFT;
	
	public static void setFocusedContainer(FocusContainer container) {
		if(focusedContainer != container) {
			focusedContainer = container;
			updateIMState(focusedContainer);
		}
	}
	
	public static FocusContainer getFocusedContainer() {
		return focusedContainer;
	}
	
	public static void setWindowFocused(boolean windowFocused) {
		isWindowFocused = windowFocused;
		updateIMState(focusedContainer);
	}
	
	public static void requestUpdateIMState(FocusContainer requestContainer) {
		if(focusedContainer == requestContainer) {
			updateIMState(requestContainer);
		}
	}
	
	public static void requestUpdateEnglishState(FocusContainer requestContainer) {
		if(focusedContainer == requestContainer) {
			updateEnglishState(requestContainer);
		}
	}
	
	private static void updateIMState(FocusContainer focusContainer) {
		if(isWindowFocused) {
			boolean preferredIMState = focusContainer.getPreferredIMState();
			IMManager.setState(preferredIMState);
			if(preferredIMState) {
				IMManager.setEnglishState(focusContainer.getPreferredEnglishState());
			}
		}
	}
	
	private static void updateEnglishState(FocusContainer focusContainer) {
		if(isWindowFocused) {
			IMManager.setEnglishState(focusContainer.getPreferredEnglishState());
		}
	}
}
