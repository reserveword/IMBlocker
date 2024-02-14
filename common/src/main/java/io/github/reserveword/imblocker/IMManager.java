package io.github.reserveword.imblocker;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public class IMManager {

    private static native WinNT.HANDLE ImmGetContext(WinDef.HWND hwnd);

    private static native WinNT.HANDLE ImmAssociateContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native boolean ImmReleaseContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native WinNT.HANDLE ImmCreateContext();

    private static native boolean ImmDestroyContext(WinNT.HANDLE himc);

    private static final boolean isWin;

    static {
        isWin = System.getProperty("os.name").toLowerCase().contains("win");
        if (isWin) {
            Native.register("imm32");
        }
    }

    private static final User32 u = User32.INSTANCE;

    private static boolean state = true;

    private static void makeOnImp() {
        if (isWin) {
            WinDef.HWND hwnd = u.GetForegroundWindow();
            WinNT.HANDLE himc = ImmGetContext(hwnd);
            if (himc == null) {
                himc = ImmCreateContext();
                ImmAssociateContext(hwnd, himc);
            }
            ImmReleaseContext(hwnd, himc);
        }
    }

    private static void makeOffImp() {
        if (isWin) {
            WinDef.HWND hwnd = u.GetForegroundWindow();
            WinNT.HANDLE himc = ImmAssociateContext(hwnd, null);
            if (himc != null) {
                ImmDestroyContext(himc);
            }
            ImmReleaseContext(hwnd, himc);
        }
    }

    private static boolean toggleImp() {
        if (isWin) {
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
        } else {
            return true; // always make im on
        }
    }

    public static void makeOn() {
        if (!state) {
            makeOnImp();
            state = true;
        }
    }

    public static void makeOff() {
        if (state) {
            makeOffImp();
            state = false;
        }
    }

    public static void makeState(boolean on) {
        if (state == on) return;
        if (on) {
            makeOnImp();
            state = true;
        } else {
            makeOffImp();
            state = false;
        }
    }

    public static void syncState() {
        if (isWin) {
            WinDef.HWND hwnd = u.GetForegroundWindow();
            WinNT.HANDLE himc = ImmGetContext(hwnd);
            if ((himc == null) == state) {
                Common.LOGGER.warn("IM state inconsistent! state={}, im={}", state, himc != null);
                toggle();
            }
        }
    }

    public static boolean getState() {
        return state;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean toggle() {
        state = toggleImp();
        return state;
    }
}
