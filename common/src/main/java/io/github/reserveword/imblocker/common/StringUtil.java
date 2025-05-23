package io.github.reserveword.imblocker.common;

public class StringUtil {

	public static String getSubstring(String string, int beginIndex, int endIndex) {
		try {
			return string.substring(beginIndex, endIndex);
		} catch (Exception e) {
			return "";
		}
	}
}
