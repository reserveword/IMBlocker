package io.github.reserveword.imblocker.common;

public class MathHelper {
	
	public static int clamp(int value, int min, int max) {
		return value < min ? min : (value > max ? max : value);
	}
	
	public static int clampLong(long value) {
		int min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;
		return value < min ? min : (value > max ? max : (int) value);
	}
}
