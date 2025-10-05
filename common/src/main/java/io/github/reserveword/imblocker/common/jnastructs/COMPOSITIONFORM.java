package io.github.reserveword.imblocker.common.jnastructs;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

@FieldOrder({"dwStyle", "ptCurrentPos", "rcArea"})
public class COMPOSITIONFORM extends Structure {
	public int dwStyle;
	public POINT ptCurrentPos;
	public RECT rcArea;
	
	public COMPOSITIONFORM() {
		ptCurrentPos = new POINT();
		rcArea = new RECT();
	}
}
