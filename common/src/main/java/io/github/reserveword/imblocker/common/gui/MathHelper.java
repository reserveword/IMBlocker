package io.github.reserveword.imblocker.common.gui;

public class MathHelper {
	
	public static int clamp(int value, int min, int max) {
		return value < min ? min : (value > max ? max : value);
	}
}
