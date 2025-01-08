package io.github.reserveword.imblocker.common;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

final class IMManagerWindows implements IMManager.PlatformIMManager {

    private static final User32 u = User32.INSTANCE;
    private static boolean state = true;
    public static long cooldown = System.currentTimeMillis();
    private static Boolean eng = null;

    static {
        Native.register("imm32");
    }

    private static native WinNT.HANDLE ImmGetContext(WinDef.HWND hwnd);

    private static native WinNT.HANDLE ImmAssociateContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native boolean ImmReleaseContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native WinNT.HANDLE ImmCreateContext();

    private static native boolean ImmDestroyContext(WinNT.HANDLE himc);

    private static native boolean ImmSetConversionStatus(WinNT.HANDLE himc, int fdwConversion, int fdwSentence);

    @Override
    public void setEnglishState(Boolean english) {
        if (english.equals(eng)) return;
        if (System.currentTimeMillis() - cooldown < 50) return;
        WinDef.HWND hwnd = u.GetActiveWindow();
        WinNT.HANDLE himc = ImmGetContext(hwnd);
        if (himc != null) {
            ImmSetConversionStatus(himc, english ? 0 : 1, 0);
        }
        ImmReleaseContext(hwnd, himc);
        eng = english;
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
        boolean state = ImmGetContext(u.GetActiveWindow()) != null;
        if (state != on) {
            cooldown = System.currentTimeMillis();
            WinDef.HWND hwnd = u.GetActiveWindow();
            if (on) {
                WinNT.HANDLE himc = ImmGetContext(hwnd);
                if (himc == null) {
                    ImmAssociateContext(hwnd, ImmCreateContext());
                }
                ImmReleaseContext(hwnd, himc);
            } else {
                WinNT.HANDLE himc = ImmAssociateContext(hwnd, null);
                ImmDestroyContext(himc);
            }
            eng = null;
            IMManagerWindows.state = on;
        }
    }
}
