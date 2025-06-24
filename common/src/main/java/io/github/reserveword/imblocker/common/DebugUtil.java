package io.github.reserveword.imblocker.common;

public class DebugUtil {

	public static void printStackTrace() {
		for(StackTraceElement trace : Thread.currentThread().getStackTrace()) {
			System.out.println(trace);
		}
	}
}
