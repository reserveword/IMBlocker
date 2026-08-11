package io.github.reserveword.imblocker.common;

import org.lwjgl.sdl.SDLKeyboard;

import com.sun.jna.CallbackReference;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.WindowProc;
import com.sun.jna.ptr.IntByReference;

import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.UniversalEnglishStateIndicator;
import io.github.reserveword.imblocker.common.gui.UniversalIMECandidateOverlay;
import io.github.reserveword.imblocker.common.jnastructs.COMPOSITIONFORM;
import io.github.reserveword.imblocker.common.jnastructs.LOGFONTW;

final class IMManagerWindows implements IMManager.PlatformIMManager {

	private static native WinNT.HANDLE ImmGetContext(WinDef.HWND hwnd);

	private static native WinNT.HANDLE ImmAssociateContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

	private static native boolean ImmReleaseContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

	private static native WinNT.HANDLE ImmCreateContext();

	private static native boolean ImmDestroyContext(WinNT.HANDLE himc);
	
	private static native boolean ImmGetConversionStatus(WinNT.HANDLE himc, IntByReference lpfdwConversion, IntByReference lpfdwSentence);

	private static native boolean ImmSetConversionStatus(WinNT.HANDLE himc, int fdwConversion, int fdwSentence);
	
	private static native boolean ImmGetCompositionWindow(WinNT.HANDLE himc, COMPOSITIONFORM cfr);

	private static native boolean ImmSetCompositionWindow(WinNT.HANDLE himc, COMPOSITIONFORM cfr);
	
	private static native boolean ImmGetCompositionFontW(WinNT.HANDLE himc, LOGFONTW lplf);

	private static native boolean ImmSetCompositionFontW(WinNT.HANDLE himc, LOGFONTW lplf);
	
	private static native int ImmGetCandidateListW(WinNT.HANDLE himc, int deIndex, Pointer lpCandList, int dwBufLen);

	private static final int WM_IME_SETCONTEXT = 0x0281;
	private static final int WM_IME_COMPOSITION = 0x010F;
	private static final int WM_IME_ENDCOMPOSITION = 0x010E;
	private static final int WM_IME_STARTCOMPOSITION = 0x010D;
	private static final int WM_IME_NOTIFY = 0x0282;

	private static final int WM_INPUTLANGCHANGE = 0x0051;
	
	private static final long ISC_SHOWUICOMPOSITIONWINDOW = 0x80000000L;
	private static final long ISC_SHOWUICANDIDATEWINDOW = 1L;
	
	private static final int IMN_CHANGECANDIDATE = 0x0003;
	private static final int IMN_SETCONVERSIONMODE = 0x0006;
	
	public static long lastIMStateOnTimestamp = System.currentTimeMillis();
	
	private static long window;
	private static Pointer originalProc;
	private static WindowProc imeListener;

	private final SetConversionStateThread setConversionStateThread;
	private boolean preferredEnglishState = false;

	static {
		Native.register("imm32");
	}

	private static final User32 u = User32.INSTANCE;

	public IMManagerWindows() {
		setConversionStateThread = new SetConversionStateThread();
		setConversionStateThread.setDaemon(true);
		setConversionStateThread.start();
	}

	@Override
	public void setState(boolean on) {
		WinDef.HWND hwnd = u.GetActiveWindow();
		if (on) {
			WinNT.HANDLE oldHimc = ImmAssociateContext(hwnd, ImmCreateContext());
			if (oldHimc != null) {
				ImmDestroyContext(oldHimc);
			}
			if (IMBlockerCore.IS_SDL_PRESENT) {
				SDLKeyboard.SDL_StartTextInput(window); // For enabling SDL_EVENT_TEXT* events.
			}
			lastIMStateOnTimestamp = System.currentTimeMillis();
		} else {
			WinNT.HANDLE himc = ImmAssociateContext(hwnd, null);
			ImmDestroyContext(himc);
		}
		IMBlockerCore.invokeOnRenderThread(() -> UniversalEnglishStateIndicator.updateIMState(on));
	}

	@Override
	public void setEnglishState(boolean englishState) {
		preferredEnglishState = englishState;
		if (!setConversionStateThread.isScheduled) {
			setConversionStateThread.awake();
			setConversionStateThread.isScheduled = true;
		}
	}

	private void syncEnglishState() {
		if(getConversionStatusCooldown() <= 0) {
			WinDef.HWND hwnd = u.GetActiveWindow();
			WinNT.HANDLE himc = ImmGetContext(hwnd);
			if (himc != null) {
				ImmSetConversionStatus(himc, preferredEnglishState ? 0 : 1, 0);
			}
			ImmReleaseContext(hwnd, himc);
			setConversionStateThread.isScheduled = false;
		}else {
			setConversionStateThread.awake();
		}
	}

	private long getConversionStatusCooldown() {
		return 60 - (System.currentTimeMillis() - lastIMStateOnTimestamp);
	}
	
	public static void updateCompositionWindowPos(Point pos) {
		WinDef.HWND hwnd = u.GetActiveWindow();
		WinNT.HANDLE himc = ImmGetContext(hwnd);
		if (himc != null) {
			COMPOSITIONFORM cfr = new COMPOSITIONFORM();
			ImmGetCompositionWindow(himc, cfr);
			cfr.dwStyle = 2; // CFS_POINT
			cfr.ptCurrentPos.x = pos.x();
			cfr.ptCurrentPos.y = pos.y();
			ImmSetCompositionWindow(himc, cfr);
		}
		ImmReleaseContext(hwnd, himc);
	}
	
	public static void updateCompositionFontSize(int fontSize) {
		WinDef.HWND hwnd = u.GetActiveWindow();
		WinNT.HANDLE himc = ImmGetContext(hwnd);
		if (himc != null) {
			LOGFONTW lplf = new LOGFONTW();
			ImmGetCompositionFontW(himc, lplf);
			lplf.lfHeight = -fontSize;
			lplf.lfWidth = 0;
			lplf.lfWeight = 400;
			lplf.lfCharSet = 0; // ANSI_CHARSET
			lplf.lfFaceName = "Arial".toCharArray();
			ImmSetCompositionFontW(himc, lplf);
		}
		ImmReleaseContext(hwnd, himc);
	}
	
	static void initializeIngameIME(long window) {
		IMManagerWindows.window = window;
		WinDef.HWND hwnd = InputSystem.getHWND(window);
		imeListener = (_hwnd, uMsg, wParam, lParam) -> {
			if(IMBlockerConfig.INSTANCE.isIngameIMEEnabled()) {
				switch (uMsg) {
					case WM_IME_SETCONTEXT:
						lParam.setValue(lParam.longValue() & ~ISC_SHOWUICANDIDATEWINDOW);
						break;
					case WM_IME_NOTIFY:
						switch (wParam.intValue()) {
							case IMN_SETCONVERSIONMODE:
								onConversionStatusChanged();
								break;
							case IMN_CHANGECANDIDATE:
								onCandidateChanged();
								break;
						}
						break;
				}
			}else if(IMBlockerConfig.INSTANCE.isClassicCompositionStyle()) {
				switch (uMsg) {
					case WM_IME_SETCONTEXT:
						LRESULT ret = u.CallWindowProc(originalProc, _hwnd, uMsg, wParam, lParam);
						lParam.setValue(lParam.longValue() | ISC_SHOWUICOMPOSITIONWINDOW);
						return ret;
					case WM_IME_COMPOSITION:
					case WM_IME_STARTCOMPOSITION:
					case WM_IME_ENDCOMPOSITION:
						return u.DefWindowProc(hwnd, uMsg, wParam, lParam);
					case WM_INPUTLANGCHANGE:
						IMBlockerCore.invokeLater(IMManager::updateCompositionFontSize);
						break;
				}
			}
			return u.CallWindowProc(originalProc, _hwnd, uMsg, wParam, lParam);
		};
		originalProc = u.SetWindowLongPtr(hwnd, WinUser.GWL_WNDPROC, CallbackReference.getFunctionPointer(imeListener));
		IMBlockerCore.invokeLater(FocusManager::initializeWindowFocus);
	}
	
	private static void onConversionStatusChanged() {
		WinDef.HWND hwnd = u.GetActiveWindow();
		WinNT.HANDLE himc = ImmGetContext(hwnd);
		if(himc != null) {
			IntByReference conversion = new IntByReference();
			IntByReference sentence = new IntByReference();
			ImmGetConversionStatus(himc, conversion, sentence);
			IMBlockerCore.invokeOnRenderThread(() -> UniversalEnglishStateIndicator
					.updateEnglishState(conversion.getValue() == 0));
		}
		ImmReleaseContext(hwnd, himc);
	}
	
	static void onCandidateChanged() {
		WinDef.HWND hwnd = u.GetActiveWindow();
		WinNT.HANDLE himc = ImmGetContext(hwnd);
		if (himc != null) {
			int bufferSize = ImmGetCandidateListW(himc, 0, Pointer.NULL, 0);
			if(bufferSize != 0) {
				Memory buffer = new Memory(bufferSize);
				ImmGetCandidateListW(himc, 0, buffer, bufferSize);
				int selectedIndex = buffer.getInt(12);
				int pageStart = buffer.getInt(16);
				int pageSize = buffer.getInt(20);
				String[] selectedPageCandidates = new String[pageSize];
				for(int i = pageStart; i < pageStart + pageSize; i++) {
					selectedPageCandidates[i - pageStart] = buffer.getWideString(buffer.getInt(24 + i * 4));
				}
				IMBlockerCore.invokeOnRenderThread(() -> UniversalIMECandidateOverlay.getInstance()
						.candidateListUpdated(selectedPageCandidates, selectedIndex - pageStart));
			}else {
				IMBlockerCore.invokeOnRenderThread(() -> UniversalIMECandidateOverlay.getInstance()
						.candidateListUpdated(null, 0));
			}
		}
		ImmReleaseContext(hwnd, himc);
	}

	private class SetConversionStateThread extends Thread {

		private boolean isScheduled = false;

		@Override
		public synchronized void run() {
			while (true) {
				await(0);

				while (true) {
					long cooldown = getConversionStatusCooldown();
					if (cooldown <= 0) {
						IMBlockerCore.invokeOnMainThread(() -> syncEnglishState());
						break;
					} else {
						await(cooldown);
					}
				}
			}
		}

		private synchronized void awake() {
			notifyAll();
		}

		private void await(long milis) {
			try {
				wait(milis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
