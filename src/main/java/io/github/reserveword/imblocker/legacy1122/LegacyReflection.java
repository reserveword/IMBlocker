package io.github.reserveword.imblocker.legacy1122;

import java.lang.reflect.Field;

final class LegacyReflection {
    private LegacyReflection() {}

    static Object findFieldValue(Object instance, Class<?> type) {
        if (instance == null) {
            return null;
        }
        Class<?> current = instance.getClass();
        while (current != null) {
            for (Field field : current.getDeclaredFields()) {
                if (type.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        return field.get(instance);
                    } catch (Throwable ignored) {
                        return null;
                    }
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }

    static int intField(Object instance, String... names) {
        if (instance == null) {
            return 0;
        }
        for (String name : names) {
            try {
                Field field = findField(instance.getClass(), name);
                if (field != null) {
                    field.setAccessible(true);
                    return field.getInt(instance);
                }
            } catch (Throwable ignored) {
                // Try the next name.
            }
        }
        return 0;
    }

    private static Field findField(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
