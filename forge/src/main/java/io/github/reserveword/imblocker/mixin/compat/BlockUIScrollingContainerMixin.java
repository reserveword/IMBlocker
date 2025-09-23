package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ldtteam.blockui.controls.TextField;
import com.ldtteam.blockui.views.ScrollingContainer;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusManager;

@Mixin(value = ScrollingContainer.class, remap = false)
public abstract class BlockUIScrollingContainerMixin {
	@Inject(method = "setScrollY", at = @At("TAIL"))
	public void onScroll(double scroll, CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.getFocusOwner() instanceof TextField) {
			IMManager.updateCompositionWindowPos();
		}
	}
}
