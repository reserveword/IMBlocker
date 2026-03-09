package io.github.reserveword.imblocker.common.gui;

public record Dimension(int width, int height) {
	public static final Dimension EMPTY = new Dimension(0, 0);
}
