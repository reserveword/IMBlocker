package io.github.reserveword.imblocker.common.gui;

import imgui.ImDrawList;
import imgui.ImGui;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class UniversalEnglishStateIndicator {
	
	private static final int INDICATOR_COLOR = -1;
	
	private static boolean imState;
	private static boolean englishState;
	
	public static void updateIMState(boolean state) {
		imState = state;
	}
	
	public static void updateEnglishState(boolean isEN) {
		englishState = isEN;
	}
	
	public static void renderOnMinecraftSurface(GuiGraphicsExtractor graphics) {
		FocusableObject focusOwner;
		if(IMBlockerConfig.INSTANCE.isIngameIMEEnabled() &&
				imState && englishState && ((focusOwner = FocusManager.getFocusOwner()) != null)) {
			int fontSize = focusOwner.getFontHeight() + 1;
			graphics.text(Minecraft.getInstance().font, "EN", graphics.guiWidth() - fontSize * 2, graphics.guiHeight() - fontSize * 2, INDICATOR_COLOR, false);
		}
	}
	
	public static void renderOnImGuiSurface(ImDrawList graphics) {
		FocusableObject focusOwner;
		if(IMBlockerConfig.INSTANCE.isIngameIMEEnabled() &&
				imState && englishState && ((focusOwner = FocusManager.getFocusOwner()) != null)) {
			int fontSize = focusOwner.getFontHeight() + 1;
			int indicatorWidth = (int) ImGui.calcTextSize("EN").x;
			Rectangle containerBounds = focusOwner instanceof FocusableWidget ?
					((FocusableWidget) focusOwner).getFocusContainer().getBoundsAbs() : focusOwner.getBoundsAbs();
			int indicatorX = containerBounds.width() - fontSize * 4, indicatorY = containerBounds.height() - fontSize * 4;
			graphics.addRectFilled(indicatorX - 4, indicatorY - 4, indicatorX + indicatorWidth + 3, indicatorY + fontSize + 3, ImGui.getColorU32i(0xFFFFFFFF));
			graphics.addText(indicatorX, indicatorY, ImGui.getColorU32i(0xFF000000), "EN");
		}
	}
}
