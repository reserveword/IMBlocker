package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.IMBlockerKeyBindings;
import io.github.reserveword.imblocker.common.IMManager;
import net.minecraft.client.KeyboardListener;

@Mixin(KeyboardListener.class)
public abstract class UnlockIMEKeyListener {
	@Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
	private void onKeyInvoked(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		IMManager.evaluateKeyInput(IMBlockerKeyBindings.unlockIMEKey.matches(key, scancode), action, modifiers);
	}
}
