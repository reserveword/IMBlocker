package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.TextInputManager;

@Mixin(TextInputManager.class)
public abstract class TextInputManagerMixin {
	@Inject(method = "setTextInputArea", at = @At("HEAD"), cancellable = true)
	public void disableVanillaCaretControl(CallbackInfo ci) {
		ci.cancel();
	}
	
	@Inject(method = "startTextInput", at = @At("HEAD"), cancellable = true)
	public void disableVanillaIMEControlH(CallbackInfo ci) {
		ci.cancel();
	}
	
	@Inject(method = "stopTextInput()V", at = @At("HEAD"), cancellable = true)
	public void disableVanillaIMEControlT(CallbackInfo ci) {
		ci.cancel();
	}
}
