package io.github.reserveword.imblocker.common;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import io.github.reserveword.imblocker.common.gui.UniversalEnglishStateIndicator;
import io.github.reserveword.imblocker.common.gui.UniversalIMECandidateOverlay;

final class IMManagerWindows implements IMManager.PlatformIMManager {

	private static native WinNT.HANDLE ImmGetContext(WinDef.HWND hwnd);

	private static native WinNT.HANDLE ImmAssociateContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

	private static native boolean ImmReleaseContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

	private static native WinNT.HANDLE ImmCreateContext();

	private static native boolean ImmDestroyContext(WinNT.HANDLE himc);
	
	private static native boolean ImmGetConversionStatus(WinNT.HANDLE himc, IntByReference lpfdwConversion, IntByReference lpfdwSentence);

	private static native boolean ImmSetConversionStatus(WinNT.HANDLE himc, int fdwConversion, int fdwSentence);
	
	private static native int ImmGetCandidateListW(WinNT.HANDLE himc, int deIndex, Pointer lpCandList, int dwBufLen);

	public static long lastIMStateOnTimestamp = System.currentTimeMillis();

	private final SetConversionStateThread setConversionStateThread;
	private boolean preferredEnglishState = false;

	static {
		Native.register("imm32");
	}

	private static final User32 u = User32.INSTANCE;

	public IMManagerWindows() {
		setConversionStateThread = new SetConversionStateThread();
		setConversionStateThread.start();
	}

	@Override
	public void setState(boolean on) {
		WinDef.HWND hwnd = getActiveWindow();
		WinNT.HANDLE himc = ImmGetContext(hwnd);
		if ((himc != null) != on) {
			if (on) {
				himc = ImmCreateContext();
				ImmAssociateContext(hwnd, himc);
				lastIMStateOnTimestamp = System.currentTimeMillis();
			} else {
				himc = ImmAssociateContext(hwnd, null);
				ImmDestroyContext(himc);
			}
			UniversalEnglishStateIndicator.updateIMState(on);
		}
		ImmReleaseContext(hwnd, himc);
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
			WinDef.HWND hwnd = getActiveWindow();
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

	private static WinDef.HWND getActiveWindow() {
		try {
			return u.GetActiveWindow();
		} catch (NoSuchMethodError e) {
			return u.GetForegroundWindow();
		}
	}
	
	public void updateCandidateList() {
		WinDef.HWND hwnd = getActiveWindow();
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
	
	@Override
	public void updateIMEStatus() {
		WinDef.HWND hwnd = getActiveWindow();
		WinNT.HANDLE himc = ImmGetContext(hwnd);
		if(himc != null) {
			IntByReference conversion = new IntByReference();
			IntByReference sentence = new IntByReference();
			ImmGetConversionStatus(himc, conversion, sentence);
			UniversalEnglishStateIndicator.updateEnglishState(conversion.getValue() == 0);
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
