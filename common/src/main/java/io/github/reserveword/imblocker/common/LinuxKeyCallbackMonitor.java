package io.github.reserveword.imblocker.common;

public abstract class LinuxKeyCallbackMonitor {
	private static boolean isKeyConsistentWithChar = true;
	
	public static boolean evaluateKey(int key, int action, int modifiers) {
		if(!IMBlockerConfig.INSTANCE.isLinuxKeyboardPatchEnabled()) {
			return true;
		}
		
		char c = (char) key;
		boolean isAlphabet = c >= 'A' && c <= 'Z';
		if(action != 0) {
			if(isAlphabet && ((modifiers & 14) == 0)) {
				isKeyConsistentWithChar = false;
			}
		}else {
			if(key == 256 && !isKeyConsistentWithChar) {
				isKeyConsistentWithChar = true;
				return false;
			}
		}
		return isAlphabet || isKeyConsistentWithChar;
	}
	
	public static void resetConsistency() {
		isKeyConsistentWithChar = true;
	}
}
