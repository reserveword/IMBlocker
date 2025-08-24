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
	
	@ConfigEntry.Gui.CollapsibleObject
	LinuxCompatibilitySettings linuxCompatibilitySettings = new LinuxCompatibilitySettings();
	
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
	public EnglishState getPrimaryEnglishState() {
		return basicSettings.primaryEnglishState;
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
	
	@Override
	public boolean isLinuxKeyboardPatchEnabled() {
		return linuxCompatibilitySettings.enableKeyboardPatch;
	}
	
	@Override
	public String getIBusOnArgName() {
		return linuxCompatibilitySettings.ibusOnArgName;
	}
	
	@Override
	public String getIBusOffArgName() {
		return linuxCompatibilitySettings.ibusOffArgName;
	}
	
	@Override
	public String getFcitx5OnArgName() {
		return linuxCompatibilitySettings.fcitx5OnArgName;
	}
	
	@Override
	public String getFcitx5OffArgName() {
		return linuxCompatibilitySettings.fcitx5OffArgName;
	}
	
	@Override
	public String getFcitx5OnStateName() {
		return linuxCompatibilitySettings.fcitx5OnStateName;
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
		
		@ConfigEntry.Gui.Tooltip(count = 3)
		@ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
		EnglishState primaryEnglishState = EnglishState.CJK;
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
	
	static class LinuxCompatibilitySettings {
		@ConfigEntry.Gui.Tooltip(count = 2)
		boolean enableKeyboardPatch = true;
		
		@ConfigEntry.Gui.Tooltip
		String ibusOnArgName = "libpinyin";
		
		@ConfigEntry.Gui.Tooltip
		String ibusOffArgName = "xkb:us::eng";
		
		@ConfigEntry.Gui.Tooltip
		String fcitx5OnArgName = "-o";
		
		@ConfigEntry.Gui.Tooltip
		String fcitx5OffArgName = "-c";
		
		@ConfigEntry.Gui.Tooltip
		String fcitx5OnStateName = "2";
	}
}
