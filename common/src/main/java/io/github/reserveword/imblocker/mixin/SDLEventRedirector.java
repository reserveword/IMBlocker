package io.github.reserveword.imblocker.mixin;

import org.lwjgl.sdl.SDL_Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.Window;

import io.github.reserveword.imblocker.common.SDLEventListener;

@Mixin(Window.class)
public abstract class SDLEventRedirector {
	@Inject(method = "handleEvent", at = @At("TAIL"))
	public void processSDLEvent(SDL_Event event, CallbackInfo ci) {
		SDLEventListener.processSDLEvent(event);
	}
}
