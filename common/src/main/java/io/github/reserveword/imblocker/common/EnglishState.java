package io.github.reserveword.imblocker.common;

public enum EnglishState {
	ENG(true), CJK(false);
	
	private final boolean bool;
	
	private EnglishState(boolean bool) {
		this.bool = bool;
	}
	
	public boolean getBoolean() {
		return bool;
	}
}
