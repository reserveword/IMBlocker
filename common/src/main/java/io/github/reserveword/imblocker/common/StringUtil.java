package io.github.reserveword.imblocker.common;

public class StringUtil {

	public static String getSubstring(String string, int beginIndex, int endIndex) {
		if((endIndex >= beginIndex) && (beginIndex >= 0) && (endIndex <= string.length())) {
			return string.substring(beginIndex, endIndex);
		}else {
			return "";
		}
	}
}
