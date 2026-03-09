package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.LinuxKeyCallbackMonitor;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;

@Mixin(KeyboardHandler.class)
public abstract class LinuxKeyboardPatch {
	@Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
	private void onKeyInvoked(long window, int action, KeyEvent keyEvent, CallbackInfo ci) {
		if(!LinuxKeyCallbackMonitor.evaluateKey(keyEvent.key(), action, keyEvent.modifiers())) {
			ci.cancel();
		}
	}
	
	@Inject(method = "charTyped", at = @At("HEAD"))
	private void onCharInvoked(long window, CharacterEvent characterEvent, CallbackInfo ci) {
		LinuxKeyCallbackMonitor.resetConsistency();
	}
}
