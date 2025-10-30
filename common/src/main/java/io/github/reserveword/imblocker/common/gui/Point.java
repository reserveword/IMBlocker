package io.github.reserveword.imblocker.common.gui;

public class Point {
	public static final Point TOP_LEFT = new Point(0, 0);
	
	private final int x;
	private final int y;
	
	public Point(double scaleFactor, int x, int y) {
		this((int) (scaleFactor * x), (int) (scaleFactor * y));
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Point derive(double scaleFactor) {
		return new Point(scaleFactor, x, y);
	}
	
	public int x() {
		return x;
	}
	
	public int y() {
		return y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Point) {
			Point p = (Point) obj;
			return p.x == x && p.y == y;
		}else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
}
