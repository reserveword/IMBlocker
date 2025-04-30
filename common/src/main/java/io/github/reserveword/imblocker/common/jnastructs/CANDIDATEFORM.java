package io.github.reserveword.imblocker.common.jnastructs;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.WinDef;

@FieldOrder({"dwIndex", "dwStyle", "ptCurrentPos", "rcArea"})
public class CANDIDATEFORM extends Structure {
	
	public int dwIndex;
	public int dwStyle;
	public WinDef.POINT ptCurrentPos;
	public WinDef.RECT rcArea;
	
	public CANDIDATEFORM() {
		ptCurrentPos = new WinDef.POINT();
		rcArea = new WinDef.RECT();
	}
}
