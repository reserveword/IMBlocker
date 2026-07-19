package io.github.reserveword.imblocker.common;

import org.jspecify.annotations.NonNull;
import org.lwjgl.sdl.SDL_Event;

import top.fifthlight.blazesdl.api.BlazeSDLAPI;
import top.fifthlight.blazesdl.api.BlazeSDLEventHandler;

public final class BlazeSDLBridge {
	public static void registerEventListener() {
		BlazeSDLAPI.getInstance().registerEventHandler(new BlazeSDLEventHandler() {
			@Override
			public boolean handleEvent(@NonNull SDL_Event event) {
				SDLEventListener.processSDLEvent(event);
				return true;
			}
			
			@Override
			public int getPriority() {
				return 0;
			}
		});
	}
}
