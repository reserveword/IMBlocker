package io.github.reserveword.imblocker.common;

import org.jspecify.annotations.NonNull;
import org.lwjgl.sdl.SDL_Event;

import top.fifthlight.blazesdl.api.BlazeSDLAPI;
import top.fifthlight.blazesdl.api.BlazeSDLEventHandler;

public final class BlazeSDLBridge {
	public static void registerSDLEventListener() {
		BlazeSDLAPI.getInstance().registerEventHandler(new BlazeSDLEventHandler() {
			@Override
			public boolean handleEvent(@NonNull SDL_Event event) {
				Key2CharTransformer.evaluateSDLEvent(event);
				return false;
			}
			
			@Override
			public int getPriority() {
				return 0;
			}
		});
	}
}
