package io.github.reserveword.imblocker.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.sun.jna.Platform;

public class IMBlockerConfig {
	public static IMBlockerConfig INSTANCE = new IMBlockerConfig();

	public static final List<String> defaultScreenWhitelist = Lists.newArrayList(
			"com.simibubi.create.content.equipment.clipboard.ClipboardScreen",
			"net.mehvahdjukaar.supplementaries.client.screens.TextHolderEditScreen",
			"dev.simulated_team.simulated.content.blocks.nameplate.NameplateScreen",
			"com.enxv.aeronauticsstructuretool.ToolModeScreen");

	private static final Set<Class<?>> bakedScreenWhitelist = new HashSet<>();
	
	private static Matcher commandPrefixRegexMatcher = Pattern.compile("^/").matcher("");

	public void reloadConfig() {
		reloadScreenWhitelist(Collections.emptyList());
	}
	
	void reloadScreenWhitelist(List<String> newScreenWhitelist) {
		bakedScreenWhitelist.clear();
		Set<String> rawScreenWhitelist = new HashSet<>(defaultScreenWhitelist);
		rawScreenWhitelist.addAll(newScreenWhitelist);
		for (String s : rawScreenWhitelist) {
			try {
				if (s.contains(":")) {
					String[] ss = s.split(":");
					s = ss[ss.length - 1];
				}
				bakedScreenWhitelist.add(Class.forName(s));
			} catch (ClassNotFoundException e) {
				IMBlockerCore.LOGGER.warn("[IMBlocker] Class {} not found, ignored.", s);
			} catch (Throwable e) {
				IMBlockerCore.LOGGER.warn("[IMBlocker] Invalid screen class: " + e);
			}
		}
		IMBlockerCore.LOGGER.info("[IMBlocker] bakelist {} result {}", "screenWhitelist", bakedScreenWhitelist);
	}

	public boolean isScreenInWhitelist(Object screen) {
		return bakedScreenWhitelist.stream().anyMatch(screenCls -> screenCls.isInstance(screen));
	}
	
	void reloadCommandPrefixRegex(String prefixRegex) {
		commandPrefixRegexMatcher = Pattern.compile(prefixRegex).matcher("");
	}
	
	public boolean isCommand(String text) {
		commandPrefixRegexMatcher.reset(text.trim());
		return commandPrefixRegexMatcher.find();
	}

	public void recoverScreen(String screenClsName) {
	}

	public boolean isScreenRecoveringEnabled() {
		return false;
	}
	
	public EnglishStateImpl getEnglishStateImpl() {
		return Platform.isWindows() ? EnglishStateImpl.CONVERSION_STATUS : EnglishStateImpl.DISABLE_IM;
	}
	
	public EnglishState getPrimaryEnglishState() {
		return EnglishState.CJK;
	}
	
	public boolean isCharSimulationEnabled() {
		return false;
	}
	
	public boolean sfm$showLineNumber() {
		return true;
	}

	public boolean isConversionStatusApiEnabled() {
		return true;
	}

	public boolean isCursorPositionTrackingEnabled() {
		return true;
	}

	public boolean isCompositionFontTweaksEnabled() {
		return true;
	}
	
	public boolean isIngameIMEEnabled() {
		return false;
	}
	
	public boolean isLinuxKeyboardPatchEnabled() {
		return true;
	}
	
	public String getEnglishStateOnCommand() {
		return "";
	}
	
	public String getEnglishStateOffCommand() {
		return "";
	}
	
	public String getIBusOnArgName() {
		return "libpinyin";
	}
	
	public String getIBusOffArgName() {
		return "xkb:us::eng";
	}
	
	public String getIBusOnStateName() {
		return getIBusOnArgName();
	}
	
	public String getFcitx5OnArgName() {
		return "-o";
	}
	
	public String getFcitx5OffArgName() {
		return "-c";
	}
	
	public String getFcitx5OnStateName() {
		return "2";
	}
}
