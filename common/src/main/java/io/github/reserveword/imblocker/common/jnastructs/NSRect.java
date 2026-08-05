package io.github.reserveword.imblocker.common.jnastructs;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

@FieldOrder({"x", "y", "width", "height"})
public class NSRect extends Structure implements Structure.ByValue {
	public double x;
	public double y;
	public double width;
	public double height;

	public NSRect() {}
	
	public NSRect(Pointer peer) {
		super(peer);
		read();
	}

	public NSRect(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("x", "y", "width", "height");
	}
}
