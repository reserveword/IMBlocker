package io.github.reserveword.imblocker.legacy1122;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.CallbackReference;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;
import com.sun.jna.win32.W32APIOptions;

import java.util.Arrays;
import java.util.List;

/**
 * Windows IMM32 backend shared by Microsoft's IME and third-party IMM32 IMEs.
 * The backend changes only the native conversion bit by default so vendor
 * specific flags used by Sogou, iFlytek, QQ Pinyin, Baidu and Rime survive.
 */
final class WindowsImeBackend implements ImeBackend {
    private static final int CFS_POINT = 0x0002;
    private static final int IME_CMODE_NATIVE = 0x0001;
    private static final int IME_CMODE_FULLSHAPE = 0x0008;
    private static final int GWL_WNDPROC = -4;
    private static final int WM_KEYUP = 0x0101;
    private static final int WM_SYSKEYUP = 0x0105;
    private static final int WM_SETFOCUS = 0x0007;
    private static final int WM_ACTIVATEAPP = 0x001C;
    private static final int WM_IME_NOTIFY = 0x0282;
    private static final int WM_INPUTLANGCHANGE = 0x0051;
    private static final int IMN_SETCONVERSIONMODE = 0x0006;
    private static final int VK_SHIFT = 0x10;

    private final Imm32 imm32;
    private final User32 user32 = User32.INSTANCE;
    private final User32Ext user32Ext = User32Ext.INSTANCE;

    /**
     * The context detached from the Minecraft window while IME is disabled.
     * It is kept alive and re-associated when text input becomes active again.
     * This is important for Microsoft Pinyin, which keeps conversion state in
     * the window's existing HIMC rather than in a newly-created context.
     */
    private HANDLE detachedContext;
    private HWND managedWindow;
    private HWND hookedWindow;
    private Pointer originalProc;
    private WindowProc imeListener;

    WindowsImeBackend() {
        imm32 = Native.loadLibrary("imm32", Imm32.class);
    }

    @Override
    public boolean supportsConversionStatus() {
        return LegacyConfig.conversionStatusApi;
    }

    @Override
    public synchronized boolean setEnabled(boolean enabled) {
        HWND hwnd = activeWindow();
        if (hwnd == null) {
            return false;
        }
        if (managedWindow != null && !sameWindow(managedWindow, hwnd)) {
            detachedContext = null;
        }
        managedWindow = hwnd;
        installWindowHook(hwnd);

        if (!enabled) {
            disableOnWindow(hwnd);
            return true;
        }

        HANDLE context = detachedContext;
        boolean acquiredContext = false;
        if (context != null) {
            detachedContext = null;
            imm32.ImmAssociateContext(hwnd, context);
        } else {
            context = imm32.ImmGetContext(hwnd);
            acquiredContext = context != null;
            if (context == null) {
                context = imm32.ImmCreateContext();
                if (context != null) {
                    imm32.ImmAssociateContext(hwnd, context);
                }
            }
        }
        if (context != null) {
            imm32.ImmSetOpenStatus(context, true);
            if (acquiredContext) {
                imm32.ImmReleaseContext(hwnd, context);
            }
        }
        return true;
    }

    @Override
    public synchronized void onWindowFocusGained() {
        HWND current = discoverWindow();
        if (current == null) {
            return;
        }
        if (managedWindow == null || !sameWindow(managedWindow, current)) {
            detachedContext = null;
            managedWindow = current;
        }
        installWindowHook(current);
    }

    @Override
    public synchronized void reconcile(boolean desiredEnabled, boolean englishLocked, boolean english) {
        HWND hwnd = activeWindow();
        if (hwnd == null) {
            return;
        }
        installWindowHook(hwnd);
        if (!desiredEnabled) {
            HANDLE context = imm32.ImmGetContext(hwnd);
            if (context != null) {
                imm32.ImmReleaseContext(hwnd, context);
                disableOnWindow(hwnd);
            }
            return;
        }

        HANDLE context = imm32.ImmGetContext(hwnd);
        if (context == null) {
            setEnabled(true);
            return;
        }
        try {
            if (englishLocked) {
                if (!imm32.ImmGetOpenStatus(context)) {
                    imm32.ImmSetOpenStatus(context, true);
                }
                IntByReference conversion = new IntByReference();
                IntByReference sentence = new IntByReference();
                if (imm32.ImmGetConversionStatus(context, conversion, sentence)) {
                    int mode = conversion.getValue();
                    boolean currentlyEnglish = (mode & IME_CMODE_NATIVE) == 0;
                    if (currentlyEnglish != english) {
                        if (english) {
                            mode &= ~IME_CMODE_NATIVE;
                        } else {
                            mode |= IME_CMODE_NATIVE;
                        }
                        imm32.ImmSetConversionStatus(context, mode, sentence.getValue());
                    }
                }
            }
        } finally {
            imm32.ImmReleaseContext(hwnd, context);
        }
    }

    @Override
    public synchronized void setEnglishMode(boolean english) {
        if (!LegacyConfig.conversionStatusApi) {
            return;
        }
        HWND hwnd = activeWindow();
        if (hwnd == null) {
            return;
        }
        HANDLE context = imm32.ImmGetContext(hwnd);
        if (context == null) {
            return;
        }
        try {
            IntByReference conversion = new IntByReference();
            IntByReference sentence = new IntByReference();
            if (!imm32.ImmGetConversionStatus(context, conversion, sentence)) {
                return;
            }
            int mode = conversion.getValue();
            if (english) {
                mode &= ~IME_CMODE_NATIVE;
                if (!LegacyConfig.preserveConversionFlags) {
                    mode &= ~IME_CMODE_FULLSHAPE;
                }
            } else {
                mode |= IME_CMODE_NATIVE;
            }
            imm32.ImmSetConversionStatus(context, mode, sentence.getValue());
        } finally {
            imm32.ImmReleaseContext(hwnd, context);
        }
    }

    @Override
    public synchronized void updateCompositionWindow(int x, int y, int height) {
        HWND hwnd = activeWindow();
        if (hwnd == null) {
            return;
        }
        HANDLE context = imm32.ImmGetContext(hwnd);
        if (context == null) {
            return;
        }
        try {
            CompositionForm form = new CompositionForm();
            imm32.ImmGetCompositionWindow(context, form);
            form.dwStyle = CFS_POINT;
            form.ptCurrentPos.x = x;
            form.ptCurrentPos.y = y + LegacyConfig.compositionWindowYOffset;
            form.write();
            imm32.ImmSetCompositionWindow(context, form);
        } finally {
            imm32.ImmReleaseContext(hwnd, context);
        }
    }

    private HWND activeWindow() {
        // GetActiveWindow is thread-local and is the reliable source while
        // state changes are requested from Minecraft's client thread. The
        // conversion-status task runs on a daemon worker, so keep using the
        // last handle captured from that thread instead of accidentally
        // changing the IME belonging to a launcher, terminal, or chat app.
        if (managedWindow != null) {
            return managedWindow;
        }
        HWND hwnd = user32Ext.GetActiveWindow();
        return hwnd != null ? hwnd : user32.GetForegroundWindow();
    }

    private HWND discoverWindow() {
        HWND hwnd = user32Ext.GetActiveWindow();
        return hwnd != null ? hwnd : user32.GetForegroundWindow();
    }

    private void installWindowHook(HWND hwnd) {
        if (hwnd == null || (hookedWindow != null && sameWindow(hookedWindow, hwnd))) {
            return;
        }
        if (hookedWindow != null && originalProc != null) {
            try {
                user32.SetWindowLongPtr(hookedWindow, GWL_WNDPROC, originalProc);
            } catch (Throwable ignored) {
                // The old window may already have been destroyed.
            }
        }
        imeListener = new WindowProc() {
            @Override
            public LRESULT callback(HWND window, int message, WPARAM wParam, LPARAM lParam) {
                LRESULT result = user32Ext.CallWindowProc(originalProc, window, message, wParam, lParam);
                if ((message == WM_IME_NOTIFY && wParam.intValue() == IMN_SETCONVERSIONMODE)
                        || message == WM_INPUTLANGCHANGE
                        || message == WM_SETFOCUS
                        || message == WM_ACTIVATEAPP
                        || ((message == WM_KEYUP || message == WM_SYSKEYUP)
                        && wParam.intValue() == VK_SHIFT)) {
                    ImeManager.INSTANCE.onNativeStateChanged();
                }
                return result;
            }
        };
        Pointer callback = CallbackReference.getFunctionPointer(imeListener);
        originalProc = user32.SetWindowLongPtr(hwnd, GWL_WNDPROC, callback);
        hookedWindow = hwnd;
    }

    private void disableOnWindow(HWND hwnd) {
        HANDLE context = imm32.ImmGetContext(hwnd);
        if (context != null) {
            try {
                // ImmAssociateContext(NULL) is the operation used by the
                // official 1.16.5 implementation. ImmSetOpenStatus alone is
                // ignored by Microsoft Pinyin on some Windows 10/11 builds.
                imm32.ImmSetOpenStatus(context, false);
                IntByReference conversion = new IntByReference();
                IntByReference sentence = new IntByReference();
                if (imm32.ImmGetConversionStatus(context, conversion, sentence)) {
                    conversion.setValue(conversion.getValue() & ~IME_CMODE_NATIVE);
                    imm32.ImmSetConversionStatus(context, conversion.getValue(), sentence.getValue());
                }
            } finally {
                imm32.ImmReleaseContext(hwnd, context);
            }
        }
        HANDLE old = imm32.ImmAssociateContext(hwnd, null);
        if (old != null) {
            detachedContext = old;
        }
    }

    private static boolean sameWindow(HWND first, HWND second) {
        Pointer a = first == null ? null : first.getPointer();
        Pointer b = second == null ? null : second.getPointer();
        return a == null ? b == null : a.equals(b);
    }

    private interface Imm32 extends Library {
        HANDLE ImmGetContext(HWND hwnd);
        HANDLE ImmAssociateContext(HWND hwnd, HANDLE context);
        boolean ImmReleaseContext(HWND hwnd, HANDLE context);
        HANDLE ImmCreateContext();
        boolean ImmDestroyContext(HANDLE context);
        boolean ImmGetConversionStatus(HANDLE context, IntByReference conversion, IntByReference sentence);
        boolean ImmSetConversionStatus(HANDLE context, int conversion, int sentence);
        boolean ImmSetOpenStatus(HANDLE context, boolean open);
        boolean ImmGetOpenStatus(HANDLE context);
        boolean ImmGetCompositionWindow(HANDLE context, CompositionForm form);
        boolean ImmSetCompositionWindow(HANDLE context, CompositionForm form);
    }

    private interface User32Ext extends StdCallLibrary {
        User32Ext INSTANCE = Native.loadLibrary("user32", User32Ext.class, W32APIOptions.DEFAULT_OPTIONS);
        HWND GetActiveWindow();
        LRESULT CallWindowProc(Pointer previousProcedure, HWND hwnd, int message, WPARAM wParam, LPARAM lParam);
    }

    private interface WindowProc extends StdCallCallback {
        LRESULT callback(HWND hwnd, int message, WPARAM wParam, LPARAM lParam);
    }

    public static class CompositionForm extends Structure {
        public int dwStyle;
        public Point ptCurrentPos;
        public Rect rcArea;

        public CompositionForm() {
            ptCurrentPos = new Point();
            rcArea = new Rect();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("dwStyle", "ptCurrentPos", "rcArea");
        }
    }

    public static class Point extends Structure {
        public int x;
        public int y;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("x", "y");
        }
    }

    public static class Rect extends Structure {
        public int left;
        public int top;
        public int right;
        public int bottom;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("left", "top", "right", "bottom");
        }
    }
}
