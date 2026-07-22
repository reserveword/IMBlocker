package io.github.reserveword.imblocker.mixin;

import org.lwjgl.sdl.SDL_Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.Key2CharTransformer;

@Mixin(targets = "com.mojang.blaze3d.platform.SDLEventHandler")
public abstract class SDLKey2CharPatch {
	@Inject(method = "handleKeyEvent", at = @At("TAIL"))
	private void onKeyEvent(SDL_Event event, CallbackInfo ci) {
		Key2CharTransformer.evaluateSDLEvent(event);
	}
}
