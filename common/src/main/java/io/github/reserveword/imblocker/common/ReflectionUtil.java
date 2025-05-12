package io.github.reserveword.imblocker.common;

import java.lang.reflect.Field;

public class ReflectionUtil {
	
	@SuppressWarnings("unchecked")
	public static <R> R getFieldValue(Class<?> cls, Object instance, Class<R> retType, String fieldName) {
		try {
			Field field = cls.getDeclaredField(fieldName);
			field.setAccessible(true);
			return (R) field.get(instance);
		} catch (Exception e) {
			return null;
		}
	}
}
