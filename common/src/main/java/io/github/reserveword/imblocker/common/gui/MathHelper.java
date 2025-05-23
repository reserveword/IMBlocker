package io.github.reserveword.imblocker.common.gui;

public class MathHelper {
	
	public static int clamp(int value, int min, int max) {
		return Math.min(Math.max(value, min), max);
	}
}
