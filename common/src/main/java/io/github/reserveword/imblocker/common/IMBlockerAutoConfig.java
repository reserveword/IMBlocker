package io.github.reserveword.imblocker.common;

import java.util.ArrayList;
import java.util.function.Supplier;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = Common.MODID)
public class IMBlockerAutoConfig extends IMBlockerConfig implements ConfigData {
	
	@ConfigEntry.Gui.Tooltip
    ArrayList<String> screenWhitelist = new ArrayList<>(defaultScreenWhitelist);
	
    boolean enableScreenRecovering = false;
    
    @ConfigEntry.Gui.Tooltip
    ArrayList<String> recoveredScreens = new ArrayList<>();
    
    @ConfigEntry.Gui.Tooltip(count = 2)
    CommandInputMode commandInputMode = CommandInputMode.IM_ENG_STATE;
    
    @ConfigEntry.Gui.Tooltip
    boolean enableCompatibilityMode = false;
    
    @Override
    public void validatePostLoad() {
    	reloadScreenWhitelist(screenWhitelist);
    }
    
    public void recoverScreen(String screenClsName) {
    	if(!recoveredScreens.contains(screenClsName)) {
    		recoveredScreens.add(screenClsName);
    	}
    }
    
    public boolean isScreenRecoveringEnabled() {
    	return enableScreenRecovering;
    }
    
    public CommandInputMode getChatCommandInputType() {
    	return commandInputMode;
    }
    
    public boolean isCompatibilityModeEnabled() {
    	return enableCompatibilityMode;
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T getConfigScreen(T parent, Class<T> screenCls) {
    	return (T) ReflectionUtil.invokeMethod(AutoConfig.class, null, Supplier.class, "getConfigScreen", 
    			new Class[] {Class.class, screenCls}, IMBlockerAutoConfig.class, parent).get();
    }
}
