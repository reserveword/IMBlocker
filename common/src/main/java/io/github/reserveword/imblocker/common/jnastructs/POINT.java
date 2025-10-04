package io.github.reserveword.imblocker.common.jnastructs;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

@FieldOrder({"x", "y"})
public class POINT extends Structure {
	public int x;
	public int y;
	
	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("x", "y");
	}
}
