package io.github.reserveword.imblocker;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import io.github.reserveword.imblocker.common.IMBlockerAutoConfig;
import net.minecraft.client.gui.screen.Screen;

public class IMBlockerModMenuIntegration implements ModMenuApi {
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return IMBlocker.hasClothConfig() ? parent -> IMBlockerAutoConfig.getConfigScreen(parent, Screen.class)
				: ModMenuApi.super.getModConfigScreenFactory();
	}
}
