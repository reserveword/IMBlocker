package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.EnglishStateImpl;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerKeyBindings;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.FocusableObject;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;

@Mixin(KeyboardHandler.class)
public abstract class UnlockIMEKeyListener {
	private boolean unlockPending = false;
	
	@Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
	private void onKeyInvoked(long window, int action, KeyEvent keyEvent, CallbackInfo ci) {
		imblocker$evaluateKeyInput(IMBlockerKeyBindings.unlockIMEKey.matches(keyEvent), action, keyEvent.modifiers());
	}
	
	private void imblocker$evaluateKeyInput(boolean isUnlockIMEKey, int action, int modifiers) {
		if(IMBlockerConfig.INSTANCE.getEnglishStateImpl() == EnglishStateImpl.DISABLE_IM) {
			if(isUnlockIMEKey) {
				if((modifiers & 14) == 0) {
					if(action == 1) {
						unlockPending = true;
					}else if(action == 0 && unlockPending) {
						FocusableObject focusOwner = FocusManager.getFocusOwner();
						if(focusOwner != null && focusOwner.getPreferredState()) {
							IMManager.setState(true);
						}
						unlockPending = false;
					}
				}
			}else {
				unlockPending = false;
			}
		}
	}
}
