package io.github.reserveword.imblocker.common.gui;

public record Point(int x, int y) {
	public static final Point TOP_LEFT = new Point(0, 0);
	
	public Point(double scaleFactor, int x, int y) {
		this((int) (scaleFactor * x), (int) (scaleFactor * y));
	}
}
