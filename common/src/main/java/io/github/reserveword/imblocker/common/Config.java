package io.github.reserveword.imblocker.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public abstract class Config {
    public static final Pattern classNamePattern = Pattern.compile("^([\\p{L}_][\\p{L}\\p{N}_]*:)?([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*$");

    public static final Predicate<Object> checkClassForName =
            str -> (str instanceof String) && classNamePattern.matcher((String) str).matches();

    public static Config INSTANCE = null;
    
    private final Set<Class<?>> screenWhitelist = new HashSet<>();
    
    public void reloadScreenWhitelist(List<? extends String> newScreenWhitelist) {
    	screenWhitelist.clear();
    	for (String s: newScreenWhitelist) {
            try {
                if (s.contains(":")) {
                    String[] ss = s.split(":");
                    s = ss[ss.length - 1];
                }
                screenWhitelist.add(Class.forName(s));
            } catch (ClassNotFoundException e) {
                Common.LOGGER.warn("Class {} not found, ignored.", s);
            } catch (Throwable e) {
                Common.LOGGER.warn(e);
            }
        }
        Common.LOGGER.info("imblocker bakelist {} result {}", "screenWhitelist", screenWhitelist);
    }
    
    public boolean isScreenInWhitelist(Object screen) {
    	for(Class<?> screenCls : screenWhitelist) {
    		if(screenCls.isInstance(screen)) {
    			return true;
    		}
    	}
        return false;
    }
    
    public abstract void recoverScreen(String screenClsName);
    
    public boolean isScreenRecoveringEnabled() {
    	return false;
    }
    
    public abstract ChatCommandInputType getChatCommandInputType();
}
