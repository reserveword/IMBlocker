package io.github.reserveword.imblocker.common;

import com.sun.jna.Platform;

import io.github.reserveword.imblocker.common.gui.EfficientIMEPreeditOverlay;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.FocusableObject;
import io.github.reserveword.imblocker.common.gui.FocusableWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;

public final class IMManager {
	private static final PlatformIMManager INSTANCE;
	
	public interface PlatformIMManager {
		
		void setState(boolean on);
		
		void setEnglishState(boolean isEN);
	}
	
	private IMManager() {}
	
	public static void setState(boolean on) {
		IMBlockerCore.invokeOnMainThread(() -> {
			INSTANCE.setState(on);
			if(on) {
				updateCaretPosition();
			}
		});
	}
	
	public static void setEnglishState(boolean isEN) {
		if(IMBlockerConfig.INSTANCE.isConversionStatusApiEnabled()) {
			IMBlockerCore.invokeOnMainThread(() -> INSTANCE.setEnglishState(isEN));
		}
	}
	
	public static void updateCaretPosition() {
		FocusableObject focusedWidget = FocusManager.getFocusOwner();
		if (focusedWidget != null) {
			Point caretPos = calculateCaretPos(focusedWidget);
			EfficientIMEPreeditOverlay.getInstance().
					updateCaretPosition(caretPos.x(), caretPos.y());
		}
	}
	
	private static Point calculateCaretPos(FocusableObject inputEntry) {
		try {
			Rectangle inputEntryBounds = inputEntry.getBoundsAbs();
			Point caretPos = inputEntry.getCaretPos();
			if(inputEntryBounds == Rectangle.EMPTY && caretPos == Point.TOP_LEFT) {
				return Point.TOP_LEFT;
			}
			//Constrained to entry border.
			int caretX = MathHelper.clamp(caretPos.x(), 0, inputEntryBounds.width());
			int caretY = MathHelper.clamp(caretPos.y(), 0, inputEntryBounds.height());
			int compositionWindowPosX = inputEntryBounds.x() + caretX;
			int compositionWindowPosY = inputEntryBounds.y() + caretY;
			if(inputEntry instanceof FocusableWidget) {
				//Constrained to container border.
				FocusableWidget inputWidget = (FocusableWidget) inputEntry;
				Rectangle containerBounds = inputWidget.getFocusContainer().getBoundsAbs();
				compositionWindowPosX = MathHelper.clamp(compositionWindowPosX, 0, containerBounds.width());
				compositionWindowPosY = MathHelper.clamp(compositionWindowPosY, 0, containerBounds.height());
				compositionWindowPosX += containerBounds.x();
				compositionWindowPosY += containerBounds.y();
			}
			return new Point(compositionWindowPosX, compositionWindowPosY);
		} catch (Throwable e) {
			IMBlockerCore.LOGGER.error("[IMBlocker] Failed to calculate caret position: " + e);
			return Point.TOP_LEFT;
		}
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
