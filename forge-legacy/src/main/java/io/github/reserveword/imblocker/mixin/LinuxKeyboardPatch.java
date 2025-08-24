package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.LinuxKeyCallbackMonitor;
import net.minecraft.client.KeyboardListener;

@Mixin(KeyboardListener.class)
public abstract class LinuxKeyboardPatch {
	@Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
	private void onKeyInvoked(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		if(!LinuxKeyCallbackMonitor.evaluateKey(key, action, modifiers)) {
			ci.cancel();
		}
	}
	
	@Inject(method = "charTyped", at = @At("HEAD"))
	private void onCharInvoked(long window, int codePoint, int modifiers, CallbackInfo ci) {
		LinuxKeyCallbackMonitor.resetConsistency();
	}
}
