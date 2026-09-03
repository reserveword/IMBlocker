package io.github.reserveword.imblocker.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.sun.jna.Platform;

public class IMBlockerConfig {
	public static final Pattern classNamePattern = Pattern
			.compile("^([\\p{L}_][\\p{L}\\p{N}_]*:)?([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*$");

	public static final Predicate<Object> checkClassForName = str -> (str instanceof String)
			&& classNamePattern.matcher((String) str).matches();

	public static IMBlockerConfig INSTANCE = new IMBlockerConfig();

	private static final List<String> defaultScreenWhitelist = Lists.newArrayList(
			"net.mehvahdjukaar.supplementaries.client.screens.TextHolderEditScreen",
			"com.simibubi.create.content.equipment.clipboard.ClipboardScreen");
	private static final Set<Class<?>> bakedScreenWhitelist = new HashSet<>();
	
	private static final List<String> defaultCharSimulationScreens = Lists.newArrayList(
			"journeymap.client.ui.fullscreen.Fullscreen");
	private static final Set<Class<?>> bakedCharSimulationScreens = new HashSet<>();
	
	private static Matcher commandPrefixRegexMatcher = Pattern.compile("^/").matcher("");
	
	public void reloadConfig() {
		reloadScreenWhitelist(Collections.emptyList());
		reloadCharSimulationScreens(Collections.emptyList());
	}

	void reloadScreenWhitelist(List<? extends String> customScreenWhitelist) {
		bakeList(defaultScreenWhitelist, customScreenWhitelist, bakedScreenWhitelist, "screenWhitelist");
	}
	
	void reloadCharSimulationScreens(List<? extends String> customCharSimulationScreens) {
		bakeList(defaultCharSimulationScreens, customCharSimulationScreens, bakedCharSimulationScreens, "charSimulationScreens");
	}
	
	private void bakeList(List<String> defaultList, List<? extends String> customList,
			Set<Class<?>> dest, String listName) {
		dest.clear();
		Set<String> rawScreenWhitelist = new HashSet<>(defaultList);
		rawScreenWhitelist.addAll(customList);
		ClassLoader classLoader = getClass().getClassLoader();
		for (String s : rawScreenWhitelist) {
			try {
				if (s.contains(":")) {
					String[] ss = s.split(":");
					s = ss[ss.length - 1];
				}
				dest.add(Class.forName(s, false, classLoader));
			} catch (ClassNotFoundException e) {
				IMBlockerCore.LOGGER.warn("[IMBlocker] Class {} not found, ignored.", s);
			} catch (Throwable e) {
				IMBlockerCore.LOGGER.warn("[IMBlocker] Invalid screen class: " + e);
			}
		}
		IMBlockerCore.LOGGER.info("[IMBlocker] bakelist {} result {}", listName, dest);
	}

	public boolean isScreenInWhitelist(Object screen) {
		return bakedScreenWhitelist.stream().anyMatch(screenCls -> screenCls.isInstance(screen));
	}
	
	public boolean isCharSimulationPreferred(Object screen) {
		return screen != null && bakedCharSimulationScreens.contains(screen.getClass());
	}
	
	void reloadCommandPrefixRegex(String prefixRegex) {
		commandPrefixRegexMatcher = Pattern.compile(prefixRegex).matcher("");
	}
	
	public boolean isCommand(String text) {
		commandPrefixRegexMatcher.reset(text);
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
	
	public boolean useStrictCursorRect() {
		return false;
	}
	
	public float getExtraScale() {
		return 1.0F;
	}

	public boolean isConversionStatusApiEnabled() {
		return true;
	}
	
	public boolean isClassicCompositionStyle() {
		return false;
	}
	
	public boolean isIngameIMEEnabled() {
		return false;
	}
	
	public boolean isLinuxKeyboardPatchEnabled() {
		return true;
	}
	
	public boolean isHeadlessPreeditMode() {
		return false;
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
