package io.github.reserveword.imblocker.common.jnastructs;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

@FieldOrder({"x", "y"})
public class POINT extends Structure {
	public int x;
	public int y;
}
