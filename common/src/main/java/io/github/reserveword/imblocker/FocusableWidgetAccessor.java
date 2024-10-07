package io.github.reserveword.imblocker;

public interface FocusableWidgetAccessor
{
	boolean isWidgetEditable();
	
	default String getText() {
		return "";
	}
}
