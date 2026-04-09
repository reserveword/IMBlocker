package io.github.reserveword.imblocker.common.jnaext;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface User32Ext extends StdCallLibrary, WinUser, WinNT {
	
	@SuppressWarnings("deprecation")
	User32Ext INSTANCE = Native.loadLibrary("user32", User32Ext.class, W32APIOptions.DEFAULT_OPTIONS);
	
	LRESULT CallWindowProc(Pointer lpPrevWndFunc, HWND hWnd, int Msg, WPARAM wParam, LPARAM lParam);
}
