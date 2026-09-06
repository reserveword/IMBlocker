package io.github.reserveword.imblocker.common.gui;

public record CaretInfo(int caretX, int caretY, int inputHeight) {
	public static final CaretInfo EMPTY = new CaretInfo(0, 0, 0);
}
