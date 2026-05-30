package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class CurrentScreenInfoOverlay {
	static Screen currentScreen;
	
	public static void renderScreenClassName(GuiGraphicsExtractor graphics) {
		if(IMBlockerConfig.INSTANCE.isScreenRecoveringEnabled() && currentScreen != null) {
			graphics.text(Minecraft.getInstance().font, currentScreen.getClass().getName(), 2, 2, -1, false);
		}
	}
}
