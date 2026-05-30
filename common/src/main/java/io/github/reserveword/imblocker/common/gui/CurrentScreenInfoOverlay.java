package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class CurrentScreenInfoOverlay {
	public static void renderScreenClassName(GuiGraphics graphics) {
		if(IMBlockerConfig.INSTANCE.isScreenRecoveringEnabled()) {
			Screen currentScreen = Minecraft.getInstance().screen;
			if(currentScreen != null) {
				graphics.drawString(Minecraft.getInstance().font, currentScreen.getClass().getName(), 2, 2, -1, false);
			}
		}
	}
}
