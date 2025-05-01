package io.github.reserveword.imblocker.common;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public abstract class Config {
    public static final Pattern classNamePattern = Pattern.compile("^([\\p{L}_][\\p{L}\\p{N}_]*:)?([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*$");

    public static final Predicate<Object> checkClassForName =
            str -> (str instanceof String) && classNamePattern.matcher((String) str).matches();

    public static Config INSTANCE = null;

    public boolean inScreenWhitelist(Class<?> cls) {
        return false;
    }
    
    public abstract ChatCommandInputType getChatCommandInputType();
}
