package io.github.reserveword.imblocker.common;

final class IMManagerStub implements IMManager.PlatformIMManager {
	private static boolean state = true;

	@Override
	public void setState(boolean on) {
		if (state != on) {
			state = on;
		}
	}

	@Override
	public void setEnglishState(boolean isEN) {
		
	}
}
