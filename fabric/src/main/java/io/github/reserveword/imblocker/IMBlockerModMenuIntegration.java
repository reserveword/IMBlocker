package io.github.reserveword.imblocker;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import io.github.reserveword.imblocker.common.IMBlockerAutoConfig;
import io.github.reserveword.imblocker.common.gui.NoClothConfigAlertScreen;

public class IMBlockerModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return IMBlocker.hasClothConfig() 
				? IMBlockerAutoConfig::getConfigScreen
				: NoClothConfigAlertScreen::new;
	}
}
