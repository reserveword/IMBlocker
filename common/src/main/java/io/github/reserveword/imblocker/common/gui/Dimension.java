package io.github.reserveword.imblocker.common.gui;

public class Dimension {
public static final Dimension EMPTY = new Dimension(0, 0);
	
	private final int width;
	private final int height;

	public Dimension(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int width() {
		return width;
	}
	
	public int height() {
		return height;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Dimension) {
			Dimension d = (Dimension) obj;
			return d.width == width && d.height == height;
		}else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "[" + width + ", " + height + "]";
	}
}
