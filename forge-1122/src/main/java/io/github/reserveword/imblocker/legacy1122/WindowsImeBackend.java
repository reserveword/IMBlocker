package io.github.reserveword.imblocker.legacy1122;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
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

    private final Imm32 imm32;
    private final User32 user32 = User32.INSTANCE;
    private final User32Ext user32Ext = User32Ext.INSTANCE;

    private HANDLE associatedContext;
    private HANDLE previousContext;
    private boolean previousContextCaptured;
    private HWND associatedWindow;

    WindowsImeBackend() {
        imm32 = Native.loadLibrary("imm32", Imm32.class);
    }

    @Override
    public boolean supportsConversionStatus() {
        return LegacyConfig.conversionStatusApi;
    }

    @Override
    public synchronized void setEnabled(boolean enabled) {
        HWND hwnd = activeWindow();
        if (hwnd == null) {
            return;
        }
        if (enabled) {
            if (associatedWindow != null && !sameWindow(associatedWindow, hwnd)) {
                detachAndDestroy(associatedWindow);
            }
            if (associatedContext == null) {
                associatedContext = imm32.ImmCreateContext();
                associatedWindow = hwnd;
                previousContext = null;
                previousContextCaptured = false;
            }
            if (associatedContext != null) {
                if (!previousContextCaptured) {
                    previousContext = imm32.ImmAssociateContext(hwnd, associatedContext);
                    previousContextCaptured = true;
                } else {
                    imm32.ImmAssociateContext(hwnd, associatedContext);
                }
                imm32.ImmSetOpenStatus(associatedContext, true);
            }
        } else {
            detachAndDestroy(hwnd);
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
        // GetActiveWindow is thread-local. Conversion updates are scheduled on
        // a daemon worker, so prefer the process foreground window first.
        HWND hwnd = user32.GetForegroundWindow();
        return hwnd != null ? hwnd : user32Ext.GetActiveWindow();
    }

    private void detachAndDestroy(HWND hwnd) {
        if (hwnd != null && previousContext != null) {
            imm32.ImmAssociateContext(hwnd, previousContext);
        }
        if (associatedContext != null) {
            imm32.ImmDestroyContext(associatedContext);
        }
        associatedContext = null;
        previousContext = null;
        previousContextCaptured = false;
        associatedWindow = null;
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
        boolean ImmGetCompositionWindow(HANDLE context, CompositionForm form);
        boolean ImmSetCompositionWindow(HANDLE context, CompositionForm form);
    }

    private interface User32Ext extends StdCallLibrary {
        User32Ext INSTANCE = Native.loadLibrary("user32", User32Ext.class, W32APIOptions.DEFAULT_OPTIONS);
        HWND GetActiveWindow();
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
