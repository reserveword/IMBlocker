package io.github.reserveword.imblocker.common.gui;

public class CaretInfo {
	public static final CaretInfo EMPTY = new CaretInfo(0, 0, 0);
	
	private final int caretX;
	private final int caretY;
	private final int inputHeight;
	
	public CaretInfo(int caretX, int caretY, int inputHeight) {
		this.caretX = caretX;
		this.caretY = caretY;
		this.inputHeight = inputHeight;
	}
	
	public int caretX() {
		return caretX;
	}
	
	public int caretY() {
		return caretY;
	}
	
	public int inputHeight() {
		return inputHeight;
	}
}
