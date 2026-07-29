package io.github.reserveword.imblocker.mixin;

import org.lwjgl.sdl.SDLHints;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.util.TimeSource.NanoTimeSource;

@Mixin(RenderSystem.class)
public abstract class SDLIMEHintTweaker {
	@Inject(method = "initBackendSystem", at = @At(value = "INVOKE", target = 
			"Lorg/lwjgl/sdl/SDLInit;SDL_Init(I)Z"))
	private static void tweakIMEHint(CallbackInfoReturnable<NanoTimeSource> cir) {
		SDLHints.SDL_SetHint("SDL_IME_IMPLEMENTED_UI", "composition");
	}
}
