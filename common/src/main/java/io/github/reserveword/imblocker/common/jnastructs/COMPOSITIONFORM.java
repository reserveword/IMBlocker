package io.github.reserveword.imblocker.common.jnastructs;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.WinDef;

@FieldOrder({"dwStyle", "ptCurrentPos", "rcArea"})
public class COMPOSITIONFORM extends Structure {

	public int dwStyle;
	public WinDef.POINT ptCurrentPos;
	public WinDef.RECT rcArea;
	
	public COMPOSITIONFORM() {
		ptCurrentPos = new WinDef.POINT();
		rcArea = new WinDef.RECT();
	}
}
