package io.github.reserveword.imblocker.common;

import org.lwjgl.glfw.GLFWNativeX11;

import com.sun.jna.Platform;

import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.FocusableObject;
import io.github.reserveword.imblocker.common.gui.FocusableWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import io.github.reserveword.imblocker.common.gui.UniversalIMECandidateOverlay;
import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;
import net.minecraft.client.Minecraft;

public final class IMManager {
	private static final PlatformIMManager INSTANCE;
	
	public interface PlatformIMManager {
		
		void setState(boolean on);
		
		void setEnglishState(boolean isEN);
	}
	
	private IMManager() {}
	
	public static void setState(boolean on) {
		IMBlockerCore.invokeOnMainThread(() -> INSTANCE.setState(on));
		LinuxKeyCallbackMonitor.syncIMState(on);
		Key2CharTransformer.syncIMState(on);
		if(on) {
			updateCaretPosition();
			updateCompositionFontSize();
		}
	}
	
	public static void setEnglishState(boolean isEN) {
		if(!Platform.isWindows() || IMBlockerConfig.INSTANCE.isConversionStatusApiEnabled()) {
			IMBlockerCore.invokeOnMainThread(() -> INSTANCE.setEnglishState(isEN));
		}
	}
	
	public static void updateCaretPosition() {
		FocusableObject focusedWidget = FocusManager.getFocusOwner();
		if (focusedWidget != null) {
			if((Platform.isLinux() && IMBlockerConfig.INSTANCE.isHeadlessPreeditMode())) {
				Point caretPos = calculateCaretPos(focusedWidget, false);
				double extraScale = IMBlockerConfig.INSTANCE.getExtraScale();
				InputSystem.setPreeditCursorRectangle(Minecraft.getInstance().getWindow().handle(), 
						caretPos.x() / extraScale, caretPos.y() / extraScale, 
						1, focusedWidget.getFontHeight() * focusedWidget.getGuiScale() / extraScale);
			}else if(hasCompositionWindow()) {
				Point caretPos = calculateCaretPos(focusedWidget, false);
				IMBlockerCore.invokeOnMainThread(() -> IMManagerWindows.updateCompositionWindowPos(caretPos));
			}else {
				Point caretPos = calculateCaretPos(focusedWidget, true);
				UniversalIMEPreeditOverlay.getInstance().updateCaretPosition(caretPos.x(), caretPos.y());
				UniversalIMECandidateOverlay.getInstance().updateCaretPosition(caretPos.x(), caretPos.y());
			}
		}
	}
	
	public static void updateCompositionFontSize() {
		FocusableObject focusedWidget = FocusManager.getFocusOwner();
		if(focusedWidget != null && hasCompositionWindow()) {
			int fontSize = (int) (focusedWidget.getFontHeight() * focusedWidget.getGuiScale());
			IMBlockerCore.invokeOnMainThread(() -> IMManagerWindows.updateCompositionFontSize(fontSize));
		}
	}
	
	private static boolean hasCompositionWindow() {
		return Platform.isWindows() && IMBlockerConfig.INSTANCE.isClassicCompositionStyle() && !IMBlockerConfig.INSTANCE.isIngameIMEEnabled();
	}
	
	private static Point calculateCaretPos(FocusableObject inputEntry, boolean isIngameIME) {
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
	
	public static void initializeIngameIME(long window) {
		IMBlockerCore.invokeOnMainThread(() -> IMManagerWindows.initializeIngameIME(window));
	}
	
	public static void onCandidateChanged() {
		IMBlockerCore.invokeOnMainThread(() -> IMManagerWindows.onCandidateChanged());
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
		}else {
			if(IMBlockerCore.IS_SDL_PRESENT) {
				INSTANCE = new IMManagerSDL(Minecraft.getInstance().getWindow().handle());
			}else {
				if(Platform.isMac()) {
					INSTANCE = new IMManagerMac();
				}else if(Platform.isLinux()) {
					PlatformIMManager linuxImpl;
					try {
						Class<?> enhancedImplClass = Class.forName("xyz.rrtt217.HDRMod.compat.imblocker.IMManagerLinuxEnhanced");
						linuxImpl = (PlatformIMManager) ReflectionUtil.newInstance(enhancedImplClass, new Class[0]);
					} catch (ClassNotFoundException e) {
						long glfwWindow = Minecraft.getInstance().getWindow().handle();
						long x11Window = GLFWNativeX11.glfwGetX11Window(glfwWindow);
						linuxImpl = x11Window != 0 ? new IMManagerX11(glfwWindow, x11Window) : new IMManagerLinux();
					}
					INSTANCE = linuxImpl;
				}else {
					IMBlockerCore.LOGGER.warn("[IMBlocker] Unsupported platform, using stub");
					INSTANCE = new IMManagerStub();
				}
			}
		}
	}
}
