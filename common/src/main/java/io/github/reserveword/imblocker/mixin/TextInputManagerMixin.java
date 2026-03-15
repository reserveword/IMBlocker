package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.TextInputManager;

@Mixin(TextInputManager.class)
public abstract class TextInputManagerMixin {
	@Inject(method = "setIMEInputMode", at = @At("HEAD"), cancellable = true)
	public void disableVanillaIMEControl(boolean value, CallbackInfo ci) {
		ci.cancel();
	}
}
