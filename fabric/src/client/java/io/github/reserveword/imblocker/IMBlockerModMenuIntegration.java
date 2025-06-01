package io.github.reserveword.imblocker;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.autoconfig.AutoConfig;

public class IMBlockerModMenuIntegration implements ModMenuApi {
	
	@Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return IMBlocker.hasClothConfig() ?
        		parent -> AutoConfig.getConfigScreen(FabricConfig.class, parent).get() :
        			ModMenuApi.super.getModConfigScreenFactory();
    }
}
