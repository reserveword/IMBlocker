package io.github.reserveword.imblocker.common.gui;

public class Rectangle {
	public static final Rectangle EMPTY = new Rectangle(0, 0, 0, 0);
	
	private final int x;
	private final int y;
	private final int width;
	private final int height;
	
	public Rectangle(double scaleFactor, int x, int y, int width, int height) {
		this((int) (scaleFactor * x), (int) (scaleFactor * y), 
				(int) (scaleFactor * width), (int) (scaleFactor * height));
	}

	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Rectangle derive(double scaleFactor) {
		return new Rectangle(scaleFactor, x, y, width, height);
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Rectangle) {
			Rectangle r = (Rectangle) obj;
			return r.x == x && r.y == y && r.width == width && r.height == height;
		}else {
			return false;
		}
	}
}
