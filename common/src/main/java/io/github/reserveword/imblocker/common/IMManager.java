package io.github.reserveword.imblocker.common;

import com.sun.jna.Platform;

import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.FocusableObject;
import io.github.reserveword.imblocker.common.gui.FocusableWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import io.github.reserveword.imblocker.common.gui.UniversalIMECandidateOverlay;
import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;

public final class IMManager {
	private static final PlatformIMManager INSTANCE;
	
	public interface PlatformIMManager {
		
		void setState(boolean on);
		
		void setEnglishState(boolean isEN);
		
		default void updateCompositionWindowPos() {}
		
		default void updateCompositionFontSize() {}
		
		default void initializeIngameIME(long window) {}
	}
	
	private IMManager() {}
	
	public static void setState(boolean on) {
		IMBlockerCore.invokeOnMainThread(() -> INSTANCE.setState(on));
		if (on) {
			updateCompositionWindowPos();
			updateCompositionFontSize();
		}
	}
	
	public static void setEnglishState(boolean isEN) {
		if(IMBlockerConfig.INSTANCE.isConversionStatusApiEnabled()) {
			IMBlockerCore.invokeOnMainThread(() -> INSTANCE.setEnglishState(isEN));
		}
	}
	
	public static void updateCompositionWindowPos() {
		if(IMBlockerConfig.INSTANCE.isIngameIMEEnabled()) {
			FocusableObject focusedWidget = FocusManager.getFocusOwner();
			if (focusedWidget != null) {
				Point caretPos = calculateCaretPos(focusedWidget, true);
				UniversalIMEPreeditOverlay.getInstance().
						updateCaretPosition(caretPos.x(), caretPos.y());
				UniversalIMECandidateOverlay.getInstance().
						updateCaretPosition(caretPos.x(), caretPos.y());
			}
		}else if(IMBlockerConfig.INSTANCE.isCursorPositionTrackingEnabled()) {
			IMBlockerCore.invokeOnMainThread(() -> INSTANCE.updateCompositionWindowPos());
		}
	}
	
	public static void updateCompositionFontSize() {
		if(IMBlockerConfig.INSTANCE.isCompositionFontTweaksEnabled() &&
				!IMBlockerConfig.INSTANCE.isIngameIMEEnabled()) {
			IMBlockerCore.invokeOnMainThread(() -> INSTANCE.updateCompositionFontSize());
		}
	}
	
	public static void initializeIngameIME(long window) {
		IMBlockerCore.invokeOnMainThread(() -> INSTANCE.initializeIngameIME(window));
	}
	
	public static void evaluateKeyInput(boolean isUnlockIMEKey, int action, int modifiers) {
		if(IMBlockerConfig.INSTANCE.getEnglishStateImpl() == EnglishStateImpl.DISABLE_IM &&
				isUnlockIMEKey && (modifiers & 14) == 0 && action == 0) {
			FocusableObject focusOwner = FocusManager.getFocusOwner();
			if(focusOwner != null && focusOwner.getPreferredState()) {
				setState(true);
			}
		}
	}
	
	public static Point calculateCaretPos(FocusableObject inputEntry, boolean isIngameIME) {
		try {
			Rectangle inputEntryBounds = inputEntry.getBoundsAbs();
			Point caretPos = inputEntry.getCaretPos();
			if(inputEntryBounds == Rectangle.EMPTY && caretPos == Point.TOP_LEFT) {
				return Point.TOP_LEFT;
			}
			//Constrained to entry border.
			int compositionWindowPosX = MathHelper.clamp(caretPos.x(), 0, inputEntryBounds.width());
			int compositionWindowPosY = MathHelper.clamp(caretPos.y(), 0, inputEntryBounds.height());
			if(!isIngameIME) {
				compositionWindowPosY -= inputEntry.getGuiScale() / 2; // Tweak yPos to fit font style.
			}
			if(inputEntry instanceof FocusableWidget inputWidget) {
				compositionWindowPosX += inputEntryBounds.x();
				compositionWindowPosY += inputEntryBounds.y();
				//Constrained to container border.
				Rectangle containerBounds = inputWidget.getFocusContainer().getBoundsAbs();
				compositionWindowPosX = MathHelper.clamp(compositionWindowPosX, 0, containerBounds.width());
				compositionWindowPosY = MathHelper.clamp(compositionWindowPosY, 0, containerBounds.height());
				if(!isIngameIME) {
					compositionWindowPosX += containerBounds.x();
					compositionWindowPosY += containerBounds.y();
				}
			}
			return new Point(compositionWindowPosX, compositionWindowPosY);
		} catch (Throwable e) {
			IMBlockerCore.LOGGER.error("[IMBlocker] Failed to calculate caret position: " + e);
			return Point.TOP_LEFT;
		}
	}
	
	static {
		if(Platform.isWindows()) {
			INSTANCE = new IMManagerWindows();
		}else if(Platform.isMac()) {
			INSTANCE = new IMManagerMac();
		}else if(Platform.isLinux()) {
			INSTANCE = new IMManagerLinux();
		}else {
			IMBlockerCore.LOGGER.warn("[IMBlocker] Unsupported platform, using stub");
			INSTANCE = new IMManagerStub();
		}
	}
}
