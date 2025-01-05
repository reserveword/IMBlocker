package io.github.reserveword.imblocker.common;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

final class IMManagerWindows implements IMManager.PlatformIMManager {

    private static final User32 u = User32.INSTANCE;
    private static boolean state = true;

    static {
        Native.register("imm32");
    }

    private static native WinNT.HANDLE ImmGetContext(WinDef.HWND hwnd);

    private static native boolean ImmReleaseContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native boolean ImmSetConversionStatus(WinNT.HANDLE himc, int fdwConversion, int fdwSentence);

    @Override
    public void setEnglishState(boolean english) {
        setState(!english);
    }

    @Override
    public void syncState() {
        // maybe we don't need to sync it anymore
    }

    @Override
    public boolean getState() {
        return state;
    }

    @Override
    public void setState(boolean on) {
        if (state == on) return;
        WinDef.HWND hwnd = u.GetActiveWindow();
        WinNT.HANDLE himc = ImmGetContext(hwnd);
        ImmSetConversionStatus(himc, on ? 1 : 0, 0);
        ImmReleaseContext(hwnd, himc);
        state = on;
    }
}
