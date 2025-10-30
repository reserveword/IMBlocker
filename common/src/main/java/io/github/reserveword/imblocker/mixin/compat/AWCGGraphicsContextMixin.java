package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.apple.library.coregraphics.CGRect;

import io.github.reserveword.imblocker.common.accessor.AWCGGraphicsContextAccessor;

@Mixin(targets = "com.apple.library.coregraphics.CGGraphicsContext", remap = false)
public abstract class AWCGGraphicsContextMixin implements AWCGGraphicsContextAccessor {
	private CGRect imblocker$currentClip;
	
	@Inject(method = "addClip(Lcom/apple/library/coregraphics/CGRect;)V", at = @At("TAIL"))
	public void updateClip(CGRect clip, CallbackInfo ci) {
		imblocker$currentClip = clip;
	}
	
	@Override
	public CGRect imblocker$getCurrentClip() {
		return imblocker$currentClip;
	}
}
