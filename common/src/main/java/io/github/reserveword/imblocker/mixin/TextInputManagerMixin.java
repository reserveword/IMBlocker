package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.TextInputManager;

@Mixin(TextInputManager.class)
public abstract class TextInputManagerMixin {
	//<26.3
	@Inject(method = "setIMEInputMode", at = @At("HEAD"), cancellable = true, require = 0)
	public void disableVanillaIMEControl(boolean mode, CallbackInfo ci) {
		ci.cancel();
	}
	
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
