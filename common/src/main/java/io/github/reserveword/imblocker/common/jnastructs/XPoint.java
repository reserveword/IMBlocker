package io.github.reserveword.imblocker.common.jnastructs;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

@FieldOrder({"x", "y"})
public class XPoint extends Structure {
	public short x;
	public short y;
	
	public XPoint() {}
	public XPoint(int x, int y) {
		this.x = (short) x;
		this.y = (short) y;
	}
	
	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("x", "y");
	}
}
