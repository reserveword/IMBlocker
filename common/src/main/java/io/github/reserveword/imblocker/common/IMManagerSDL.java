package io.github.reserveword.imblocker.common;

import org.lwjgl.sdl.SDLKeyboard;

public final class IMManagerSDL implements IMManager.PlatformIMManager {
	
	private final long window;
	
	public IMManagerSDL(long window) {
		this.window = window;
	}

	@Override
	public void setState(boolean on) {
		if(on) {
			SDLKeyboard.SDL_StartTextInput(window);
		}else {
			SDLKeyboard.SDL_StopTextInput(window);
		}
	}

	@Override
	public void setEnglishState(boolean isEN) {
		//TODO Windows/Linux.
	}
}
