package io.github.reserveword.imblocker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.regex.Pattern;

public class Common {
    public static final String MODID = "imblocker";
    public static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern textFieldPattern = Pattern.compile(".*(TextField|EditBox|EditText)[^.]*$", Pattern.CASE_INSENSITIVE);
    private static final HashMap<Class<?>, Boolean> textFieldCache = new HashMap<>();

    public static boolean classIsTextField(Class<?> c) {
        if (textFieldCache.containsKey(c)) {
            return textFieldCache.get(c);
        }
        boolean result;
        if (c == null) {
            result = false;
        } else if (textFieldPattern.matcher(c.getName()).matches()) {
            result = true;
        } else {
            result = classIsTextField(c.getSuperclass());
        }
        textFieldCache.put(c, result);
        return result;
    }
}
