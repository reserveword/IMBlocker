package io.github.reserveword.imblocker.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class IMBlockerConfig {
    public static final Pattern classNamePattern = Pattern.compile("^([\\p{L}_][\\p{L}\\p{N}_]*:)?([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*$");

    public static final Predicate<Object> checkClassForName =
            str -> (str instanceof String) && classNamePattern.matcher((String) str).matches();

    public static IMBlockerConfig INSTANCE = null;
	
	public static final List<String> defaultScreenWhitelist = new ArrayList<>();
    
    private static final Set<Class<?>> bakedScreenWhitelist = new HashSet<>();
    
    public void reloadScreenWhitelist(List<? extends String> newScreenWhitelist) {
    	bakedScreenWhitelist.clear();
    	for (String s: newScreenWhitelist) {
            try {
                if (s.contains(":")) {
                    String[] ss = s.split(":");
                    s = ss[ss.length - 1];
                }
                bakedScreenWhitelist.add(Class.forName(s));
            } catch (ClassNotFoundException e) {
                Common.LOGGER.warn("Class {} not found, ignored.", s);
            } catch (Throwable e) {
                Common.LOGGER.warn(e);
            }
        }
        Common.LOGGER.info("imblocker bakelist {} result {}", "screenWhitelist", bakedScreenWhitelist);
    }
    
    public boolean isScreenInWhitelist(Object screen) {
    	for(Class<?> screenCls : bakedScreenWhitelist) {
    		if(screenCls.isInstance(screen)) {
    			return true;
    		}
    	}
        return false;
    }
    
    public void recoverScreen(String screenClsName) {}
    
    public boolean isScreenRecoveringEnabled() {
    	return false;
    }
    
    public CommandInputMode getChatCommandInputType() {
    	return CommandInputMode.IM_ENG_STATE;
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
}
