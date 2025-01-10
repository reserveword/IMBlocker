package io.github.reserveword.imblocker.common;

public class SetConversionStateExecutor {
	
	private static Runnable setConversionState;
	
	public static void tick() {
		if(setConversionState != null && conversionStateCdDone()) {
			setConversionState.run();
			setConversionState = null;
		}
	}
	
	static void execute(Runnable runnable) {
		setConversionState = runnable;
	}
	
	private static boolean conversionStateCdDone() {
    	return System.currentTimeMillis() - IMManagerWindows.lastIMStateOnTimestamp > 50;
    }
}
