package io.github.reserveword.imblocker.common.jnaext;

import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

public interface WindowProc extends StdCallCallback {
    LRESULT callback(HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam);
}
