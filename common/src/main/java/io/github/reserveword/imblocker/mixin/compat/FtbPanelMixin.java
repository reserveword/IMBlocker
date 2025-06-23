package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftblibrary.ui.Panel;
import io.github.reserveword.imblocker.common.IMManager;

@Mixin(value = Panel.class, remap = false)
public class FtbPanelMixin {
	
	@Inject(method = "setScrollY", at = @At("TAIL"))
	public void onScroll(double scroll, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
}
