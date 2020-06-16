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

    static {
        Native.register("imm32");
    }

    private static User32 u = User32.INSTANCE;
    //
    // public static void activateIME() {
    // WinDef.HWND hwnd = u.GetForegroundWindow();
    // IMBlocker.LOGGER.info(u.GetWindowTextLength(hwnd));
    // IMBlocker.LOGGER.info(hwnd);
    // WinNT.HANDLE himc = ImmGetContext(hwnd);
    // IMBlocker.LOGGER.info(himc);
    // if (himc != null) {
    // ImmDestroyContext(himc);
    // }
    // himc = ImmCreateContext();
    // IMBlocker.LOGGER.info(himc);
    // ImmAssociateContext(hwnd, himc);
    // ImmReleaseContext(hwnd, himc);
    // }
    //
    // public static void deactivateIME() {
    // WinDef.HWND hwnd = u.GetForegroundWindow();
    // IMBlocker.LOGGER.info(hwnd);
    // WinNT.HANDLE himc = ImmGetContext(hwnd); //useless?
    // IMBlocker.LOGGER.info(himc);
    // ImmAssociateContext(hwnd, null);
    // }
    //
    // public static boolean checkIMEState() {
    // WinDef.HWND hwnd = u.GetForegroundWindow();
    // WinNT.HANDLE himc = ImmGetContext(hwnd); //need release?
    // return himc == null;
    // }

    public static void makeOn() {
        // if (!checkIMEState()) {
        // activateIME();
        // }
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmGetContext(hwnd);
        if (himc == null) {
            himc = ImmCreateContext();
            ImmAssociateContext(hwnd, himc);
        }
        ImmReleaseContext(hwnd, himc);
    }

    public static void makeOff() {
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmAssociateContext(hwnd, null);
        if (himc != null) {
            ImmDestroyContext(himc);
        }
        ImmReleaseContext(hwnd, himc);
    }

    public static boolean toggle() {
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmAssociateContext(hwnd, null);
        if (himc == null) {
            himc = ImmCreateContext();
            ImmAssociateContext(hwnd, himc);
            ImmReleaseContext(hwnd, himc);
            return true;
        }
        ImmReleaseContext(hwnd, himc);
        return false;
    }
}
