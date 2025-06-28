package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.FocusContainer;

@Mixin(targets = "journeymap.client.ui.fullscreen.MapChat", remap = false)
public abstract class JourneyMapChatMixin {
	
	@Inject(method = "close", at = @At("TAIL"))
	public void visibilityChanged(CallbackInfo ci) {
//		FocusContainer.MINECRAFT.locateRealFocus();
	}
	
	@Inject(method = "setHidden", at = @At("TAIL"))
	public void visibilityChanged(boolean hidden, CallbackInfo ci) {
//		FocusContainer.MINECRAFT.locateRealFocus();
	}
}
