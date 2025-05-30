package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.IMManager;

@Mixin(targets = "com.ldtteam.blockui.views.ScrollingContainer", remap = false)
public class BlockUIScrollingContainerMixin {

	@Inject(method = "setScrollY", at = @At("TAIL"))
	public void onScroll(double scroll, CallbackInfoReturnable<Boolean> cir) {
		IMManager.updateCompositionWindowPos();
	}
}
