package io.github.reserveword.imblocker.common;

import java.util.ArrayList;
import java.util.function.Supplier;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;
import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;

@Config(name = IMBlockerCore.MODID)
public class IMBlockerAutoConfig extends IMBlockerConfig implements ConfigData {
	
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	BasicSettings basicSettings = new BasicSettings();
	
	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	AdvanceSettings advanceSettings = new AdvanceSettings();
	
	@ConfigEntry.Gui.CollapsibleObject
	WindowsCompatibilitySettings windowsCompatibilitySettings = new WindowsCompatibilitySettings();
	
	@Override
	public void validatePostLoad() {
		reloadScreenWhitelist(basicSettings.screenWhitelist);
	}

	@Override
	public void recoverScreen(String screenClsName) {
		if(!basicSettings.recoveredScreens.contains(screenClsName)) {
			basicSettings.recoveredScreens.add(screenClsName);
		}
	}

	@Override
	public boolean isScreenRecoveringEnabled() {
		return basicSettings.enableScreenRecovering;
	}

	@Override
	public CommandInputMode getChatCommandInputType() {
		return basicSettings.commandInputMode;
	}
	
	@Override
	public boolean isCharSimulationEnabled() {
		return advanceSettings.enableCharSimulation;
	}

	@Override
	public boolean isConversionStatusApiEnabled() {
		return windowsCompatibilitySettings.enableConversionStatusApi;
	}

	@Override
	public boolean isCursorPositionTrackingEnabled() {
		return windowsCompatibilitySettings.enableCursorPositionTracking;
	}

	@Override
	public boolean isCompositionFontTweaksEnabled() {
		return windowsCompatibilitySettings.enableCompositionFontTweaks;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getConfigScreen(T parent, Class<T> screenCls) {
		T configScreen = (T) ReflectionUtil.invokeMethod(AutoConfig.class, null, Supplier.class, 
				"getConfigScreen", new Class[] { Class.class, screenCls }, 
				IMBlockerAutoConfig.class, parent).get();
		ConfigHolder<IMBlockerAutoConfig> configHolder = AutoConfig
				.getConfigHolder(IMBlockerAutoConfig.class);
		ReflectionUtil.invokeMethod(AbstractConfigScreen.class, configScreen, null, 
				"setSavingRunnable", new Class[] { Runnable.class }, 
				new Runnable() {
					@Override
					public void run() {
						configHolder.save();
						configHolder.getConfig().validatePostLoad();
					}
				});
		return configScreen;
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
	
	static class AdvanceSettings {
		@ConfigEntry.Gui.Tooltip(count = 2)
		boolean enableCharSimulation = false;
	}

	static class WindowsCompatibilitySettings {
		@ConfigEntry.Gui.Tooltip
		boolean enableConversionStatusApi = true;

		boolean enableCursorPositionTracking = true;

		boolean enableCompositionFontTweaks = true;
	}
}
