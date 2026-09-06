package io.github.reserveword.imblocker.common.gui;

public record Rectangle(int x, int y, int width, int height) {
	public static final Rectangle EMPTY = new Rectangle(0, 0, 0, 0);
	
	public Rectangle(double scaleFactor, double x, double y, double width, double height) {
		this((int) (scaleFactor * x), (int) (scaleFactor * y), 
				(int) (scaleFactor * width), (int) (scaleFactor * height));
	}
	
	public Rectangle derive(double scaleFactor) {
		return new Rectangle(scaleFactor, x, y, width, height);
	}
}
