package io.github.reserveword.imblocker.common;

import java.util.function.Predicate;

public abstract class Config {
    public static final Predicate<Object> checkClassForName = str -> (str instanceof String) &&
            ((String) str).matches("^([\\p{L}_][\\p{L}\\p{N}_]*:)?([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*$");

    public static Config INSTANCE = null;

    public boolean inScreenBlacklist(Class<?> cls) {
        return false;
    }

    public boolean inScreenWhitelist(Class<?> cls) {
        return false;
    }

    public boolean inInputBlacklist(Class<?> cls) {
        return false;
    }

    public boolean inInputWhitelist(Class<?> cls) {
        return false;
    }

    public Integer getCheckInterval() {
        return 2;
    }

    public Boolean getUseExperimental() {
        return false;
    }

    public Boolean getCheckCommandChat() {
        return true;
    }

    public void checkScreen(Class<?> cls) {

    }
}
