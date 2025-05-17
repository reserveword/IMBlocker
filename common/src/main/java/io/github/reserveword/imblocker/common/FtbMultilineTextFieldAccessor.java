package io.github.reserveword.imblocker.common;

public interface FtbMultilineTextFieldAccessor {

	int getCursorLineIndex();
	
	int getLineBeginIndex(int lineIndex);
	
	String getText();
	
	int getCursor();
}
