package io.github.reserveword.imblocker.common;

import java.util.ArrayList;
import java.util.function.Supplier;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;

@Config(name = Common.MODID)
public class IMBlockerAutoConfig extends IMBlockerConfig implements ConfigData {
	
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	BasicSettings basicSettings = new BasicSettings();
	
	@ConfigEntry.Gui.CollapsibleObject
	CompatibilitySettings compatibilitySettings = new CompatibilitySettings();
    
    @Override
    public void validatePostLoad() {
    	reloadScreenWhitelist(basicSettings.screenWhitelist);
    }
    
    public void recoverScreen(String screenClsName) {
    	if(!basicSettings.recoveredScreens.contains(screenClsName)) {
    		basicSettings.recoveredScreens.add(screenClsName);
    	}
    }
    
    public boolean isScreenRecoveringEnabled() {
    	return basicSettings.enableScreenRecovering;
    }
    
    public CommandInputMode getChatCommandInputType() {
    	return basicSettings.commandInputMode;
    }
    
    public boolean isConversionStatusApiDisabled() {
    	return compatibilitySettings.disableConversionStatusApi;
    }
    
    public boolean isCursorPositionTrackingDisabled() {
    	return compatibilitySettings.disableCursorPositionTracking;
    }
    
    public boolean isCompositionFontTweaksDisabled() {
    	return compatibilitySettings.disableCompositionFontTweaks;
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T getConfigScreen(T parent, Class<T> screenCls) {
    	return (T) ReflectionUtil.invokeMethod(AutoConfig.class, null, Supplier.class, "getConfigScreen", 
    			new Class[] {Class.class, screenCls}, IMBlockerAutoConfig.class, parent).get();
    }
    
    static class BasicSettings {
    	@ConfigEntry.Gui.Tooltip
        ArrayList<String> screenWhitelist = new ArrayList<>(defaultScreenWhitelist);
    	
        boolean enableScreenRecovering = false;
        
        @ConfigEntry.Gui.Tooltip
        ArrayList<String> recoveredScreens = new ArrayList<>();
        
        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
        CommandInputMode commandInputMode = CommandInputMode.IM_ENG_STATE;
    }
    
    static class CompatibilitySettings {
    	@ConfigEntry.Gui.Tooltip
    	boolean disableConversionStatusApi = false;
    	
    	boolean disableCursorPositionTracking = false;
    	
    	boolean disableCompositionFontTweaks = false;
    }
}
