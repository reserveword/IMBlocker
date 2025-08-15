package io.github.reserveword.imblocker.common;

public class LinuxKeyCallbackMonitor {
	private static boolean isKeyConsistentWithChar = true;
	
	public static void glfwPrintableKeyPressed() {
		isKeyConsistentWithChar = false;
	}
	
	public static void glfwCharTyped() {
		isKeyConsistentWithChar = true;
	}
	
	public static void resetConsistency() {
		isKeyConsistentWithChar = true;
	}
	
	public static boolean isKeyConsistentWithChar() {
		return isKeyConsistentWithChar;
	}
}
