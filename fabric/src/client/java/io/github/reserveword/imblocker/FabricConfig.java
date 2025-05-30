package io.github.reserveword.imblocker;

import java.util.ArrayList;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import io.github.reserveword.imblocker.common.ChatCommandInputType;
import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.Config;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@me.shedaniel.autoconfig.annotation.Config(name = Common.MODID)
public class FabricConfig extends Config implements ModMenuApi, ConfigData {
    ArrayList<String> screenWhitelist = new ArrayList<>(FabricCommon.defaultScreenWhitelist);
    boolean enableScreenRecovering = false;
    ArrayList<String> recoveredScreens = new ArrayList<>();
    
    @ConfigEntry.Gui.Tooltip
    ChatCommandInputType chatCommandInputType = ChatCommandInputType.IM_ENG_STATE;

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(FabricConfig.class, parent).get();
    }

    @Override
    public void validatePostLoad() {
    	reloadScreenWhitelist(screenWhitelist);
    }
    
    @Override
    public void recoverScreen(String screenClsName) {
    	if(!recoveredScreens.contains(screenClsName)) {
    		recoveredScreens.add(screenClsName);
    	}
    }
    
    @Override
    public boolean isScreenRecoveringEnabled() {
    	return enableScreenRecovering;
    }
    
    @Override
    public ChatCommandInputType getChatCommandInputType() {
    	return chatCommandInputType;
    }
}
