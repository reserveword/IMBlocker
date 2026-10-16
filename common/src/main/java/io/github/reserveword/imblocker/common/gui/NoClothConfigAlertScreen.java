package io.github.reserveword.imblocker.common.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NoClothConfigAlertScreen extends AlertScreen {
	public NoClothConfigAlertScreen(Screen parent) {
		super(() -> Minecraft.getInstance().setScreenAndShow(parent), Component.empty(),
				Component.translatable("imblocker.no_cloth_alert.message"));
	}
}
