package io.github.reserveword.imblocker.common.jnastructs;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinDef.RECT;

@FieldOrder({"dwStyle", "ptCurrentPos", "rcArea"})
public class COMPOSITIONFORM extends Structure {

	public int dwStyle;
	public POINT ptCurrentPos;
	public RECT rcArea;
	
	public COMPOSITIONFORM() {
		ptCurrentPos = new POINT();
		rcArea = new RECT();
	}
	
	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("dwStyle", "ptCurrentPos", "rcArea");
	}
}
