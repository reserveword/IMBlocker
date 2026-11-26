package io.github.reserveword.imblocker.common.gui.mc;

public abstract class ComposeTextFieldCursorMonitor {
	private static boolean isCursorAtEnd = false;
	
	public static void setCursorAtEnd(boolean b) {
		isCursorAtEnd = b;
	}
	
	public static boolean isCursorAtEnd() {
		return isCursorAtEnd;
	}
}
