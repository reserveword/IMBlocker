package io.github.reserveword.imblocker.common;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.FocusableObject;
import io.github.reserveword.imblocker.common.gui.FocusableWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import io.github.reserveword.imblocker.common.jnastructs.COMPOSITIONFORM;
import io.github.reserveword.imblocker.common.jnastructs.LOGFONTW;

final class IMManagerWindows implements IMManager.PlatformIMManager {

	private static native WinNT.HANDLE ImmGetContext(WinDef.HWND hwnd);

	private static native WinNT.HANDLE ImmAssociateContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

	private static native boolean ImmReleaseContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

	private static native WinNT.HANDLE ImmCreateContext();

	private static native boolean ImmDestroyContext(WinNT.HANDLE himc);

	private static native boolean ImmSetConversionStatus(WinNT.HANDLE himc, int fdwConversion, int fdwSentence);

	private static native boolean ImmGetCompositionWindow(WinNT.HANDLE himc, COMPOSITIONFORM cfr);

	private static native boolean ImmSetCompositionWindow(WinNT.HANDLE himc, COMPOSITIONFORM cfr);

	private static native boolean ImmGetCompositionFontW(WinNT.HANDLE himc, LOGFONTW lplf);

	private static native boolean ImmSetCompositionFontW(WinNT.HANDLE himc, LOGFONTW lplf);

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
			ImmReleaseContext(hwnd, himc);
		}
	}

	@Override
	public void setEnglishState(boolean englishState) {
		preferredEnglishState = englishState;
		if (!setConversionStateThread.isScheduled) {
			if (getConversionStatusCooldown() <= 0) {
				syncEnglishState();
			} else {
				setConversionStateThread.awake();
				setConversionStateThread.isScheduled = true;
			}
		}
	}

	private void syncEnglishState() {
		WinDef.HWND hwnd = getActiveWindow();
		WinNT.HANDLE himc = ImmGetContext(hwnd);
		if (himc != null) {
			ImmSetConversionStatus(himc, preferredEnglishState ? 0 : 1, 0);
		}
		ImmReleaseContext(hwnd, himc);
		setConversionStateThread.isScheduled = false;
	}

	private long getConversionStatusCooldown() {
		return 60 - (System.currentTimeMillis() - lastIMStateOnTimestamp);
	}

	@Override
	public void updateCompositionWindowPos() {
		WinDef.HWND hwnd = getActiveWindow();
		WinNT.HANDLE himc = ImmGetContext(hwnd);
		if (himc != null) {
			FocusableObject focusedWidget = FocusManager.getFocusOwner();
			if (focusedWidget != null) {
				Point compositionWindowPos = calculateProperCompositionWindowPos(focusedWidget);
				COMPOSITIONFORM cfr = new COMPOSITIONFORM();
				ImmGetCompositionWindow(himc, cfr);
				cfr.dwStyle = 2; // CFS_POINT
				cfr.ptCurrentPos.x = compositionWindowPos.x();
				cfr.ptCurrentPos.y = compositionWindowPos.y();
				ImmSetCompositionWindow(himc, cfr);
			}
		}
		ImmReleaseContext(hwnd, himc);
	}

	@Override
	public void updateCompositionFontSize() {
		WinDef.HWND hwnd = getActiveWindow();
		WinNT.HANDLE himc = ImmGetContext(hwnd);
		if (himc != null) {
			FocusableObject focusedWidget = FocusManager.getFocusOwner();
			if (focusedWidget != null) {
				int fontSize = focusedWidget.getFontHeight();
				fontSize *= focusedWidget.getGuiScale();
				LOGFONTW lplf = new LOGFONTW();
				ImmGetCompositionFontW(himc, lplf);
				lplf.lfHeight = -fontSize;
				lplf.lfWidth = 0;
				lplf.lfWeight = 400;
				lplf.lfCharSet = 0; // ANSI_CHARSET
				lplf.lfFaceName = "Arial".toCharArray();
				ImmSetCompositionFontW(himc, lplf);
			}
		}
		ImmReleaseContext(hwnd, himc);
	}

	private WinDef.HWND getActiveWindow() {
		try {
			return u.GetActiveWindow();
		} catch (NoSuchMethodError e) {
			return u.GetForegroundWindow();
		}
	}

	private Point calculateProperCompositionWindowPos(FocusableObject inputEntry) {
		try {
			double scaleFactor = inputEntry.getGuiScale();
			Rectangle inputEntryBounds = inputEntry.getBoundsAbs();
			Point caretPos = inputEntry.getCaretPos();
			if(inputEntryBounds == Rectangle.EMPTY && caretPos == Point.TOP_LEFT) {
				return Point.TOP_LEFT;
			}
			//Constrained to entry border.
			int caretX = MathHelper.clamp(caretPos.x(), 0, (int) (inputEntryBounds.width() - 4 * scaleFactor));
			int caretY = MathHelper.clamp(caretPos.y(), 0, (int) (inputEntryBounds.height() - 4 * scaleFactor));
			caretY -= scaleFactor / 2; // Tweak yPos to fit font style.
			int compositionWindowPosX = inputEntryBounds.x() + caretX;
			int compositionWindowPosY = inputEntryBounds.y() + caretY;
			if(inputEntry instanceof FocusableWidget) {
				//Constrained to container border.
				FocusableWidget inputWidget = (FocusableWidget) inputEntry;
				Rectangle containerBounds = inputWidget.getFocusContainer().getBoundsAbs();
				compositionWindowPosX = MathHelper.clamp(compositionWindowPosX, 0, containerBounds.width() - 16);
				compositionWindowPosY = MathHelper.clamp(compositionWindowPosY, 0, containerBounds.height() - 16);
				compositionWindowPosX += containerBounds.x();
				compositionWindowPosY += containerBounds.y();
			}
			return new Point(compositionWindowPosX, compositionWindowPosY);
		} catch (Throwable e) {
			IMBlockerCore.LOGGER.error("[IMBlocker] Failed to calculate caret position: " + e);
			return Point.TOP_LEFT;
		}
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
						MinecraftClientAccessor.INSTANCE.execute(() -> syncEnglishState());
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
