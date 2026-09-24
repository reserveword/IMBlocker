package io.github.reserveword.imblocker.common.gui.imgui;

import java.util.HashSet;
import java.util.Set;

import io.github.reserveword.imblocker.common.accessor.ImGuiGraphicsAccessor;
import io.github.reserveword.imblocker.common.accessor.ImGuiIOAccessor;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.UniversalEnglishStateIndicator;
import io.github.reserveword.imblocker.common.gui.UniversalIMECandidateOverlay;
import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;

public final class ImGuiManager {
	private static final Set<ImGuiIOAccessor> imGuiIOs = new HashSet<>();
	
	private static boolean imGuiCaptureKeyboard = false;
	private static boolean imGuiTextFieldFocused = false;
	
	private static long lastTickTimestamp = 0;
	
	public static void registerImGuiIO(ImGuiIOAccessor imGuiIO) {
		imGuiIOs.add(imGuiIO);
	}

	public static void tick() {
		if(imGuiIOs.isEmpty() || (System.currentTimeMillis() - lastTickTimestamp < 50)) return;
		
		boolean isImGuiFocused = imGuiIOs.stream().anyMatch(ImGuiIOAccessor::isCaptureKeyboard);
		boolean isImGuiTextFieldFocused = imGuiIOs.stream().anyMatch(ImGuiIOAccessor::isTextFieldFocused);
		
		if(imGuiCaptureKeyboard != isImGuiFocused) {
			imGuiCaptureKeyboard = isImGuiFocused;
			FocusManager.requestFocus(isImGuiFocused ? 
					FocusContainer.IMGUI : FocusContainer.MINECRAFT);
		}
		
		if(imGuiTextFieldFocused != isImGuiTextFieldFocused) {
			imGuiTextFieldFocused = isImGuiTextFieldFocused;
			if(isImGuiTextFieldFocused) {
				FocusContainer.IMGUI.setPreferredState(true);
			}else {
				FocusContainer.IMGUI.clearFocus();
				FocusContainer.IMGUI.setPreferredState(false);
			}
		}
		
		lastTickTimestamp = System.currentTimeMillis();
	}
	
	public static void drawIMEOverlays(ImGuiGraphicsAccessor graphics) {
		if(!FocusManager.isMinecraftContextFocused()) {
			UniversalIMEPreeditOverlay.getInstance().renderOnImGuiSurface(graphics);
			UniversalIMECandidateOverlay.getInstance().renderOnImGuiSurface(graphics);
			UniversalEnglishStateIndicator.renderOnImGuiSurface(graphics);
		}
	}
}
