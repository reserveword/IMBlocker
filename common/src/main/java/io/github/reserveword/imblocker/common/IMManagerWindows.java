package io.github.reserveword.imblocker.common;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

final class IMManagerWindows implements IMManager.PlatformIMManager {

    private static final User32 u = User32.INSTANCE;
    private static boolean state = false;

    static {
        Native.register("imm32");
    }

    private static native WinNT.HANDLE ImmGetContext(WinDef.HWND hwnd);

    private static native WinNT.HANDLE ImmAssociateContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native boolean ImmReleaseContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native WinNT.HANDLE ImmCreateContext();

    private static native boolean ImmDestroyContext(WinNT.HANDLE himc);

    private static native boolean ImmSetConversionStatus(WinNT.HANDLE himc, int fdwConversion, int fdwSentence);

    private static void makeOnImp() {
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmGetContext(hwnd);
        if (himc == null) {
            himc = ImmCreateContext();
            ImmAssociateContext(hwnd, himc);
        }
        ImmReleaseContext(hwnd, himc);
    }

    private static void makeOffImp() {
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmAssociateContext(hwnd, null);
        if (himc != null) {
            ImmDestroyContext(himc);
        }
        ImmReleaseContext(hwnd, himc);
    }

    private static boolean toggleImp() {
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmGetContext(hwnd);
        if (himc == null) {
            himc = ImmCreateContext();
            ImmAssociateContext(hwnd, himc);
            ImmReleaseContext(hwnd, himc);
            return true;
        } else {
            himc = ImmAssociateContext(hwnd, null);
            ImmDestroyContext(himc);
            ImmReleaseContext(hwnd, himc);
            return false;
        }
    }

    @Override
    public void setImmOnState(boolean isEN) {
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmGetContext(hwnd);
        if (himc != null) {
            ImmSetConversionStatus(himc, isEN ? 0 : 1, 0);
        }
        ImmReleaseContext(hwnd, himc);
    }

    @Override
    public void syncState() {
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmGetContext(hwnd);
        if ((himc == null) == state) {
            Common.LOGGER.warn("IM state inconsistent! state={}, im={}", state, himc != null);
            toggle();
        }
    }

    @Override
    public boolean getState() {
        return state;
    }

    @Override
    public void setState(boolean on) {
        boolean state = ImmGetContext(u.GetForegroundWindow()) != null;
        if (state != on) {
            IMCheckState.lastIMStateChangeTimestamp = System.currentTimeMillis();
            if (on) {
                makeOnImp();
            } else {
                makeOffImp();
                IMCheckState.cancelSetConversionState();
            }
            IMManagerWindows.state = on;
        }
    }

    public boolean toggle() {
        state = toggleImp();
        return state;
    }
}
