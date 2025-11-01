package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.apple.library.coregraphics.CGAffineTransform;
import com.apple.library.coregraphics.CGGraphicsContext;

import io.github.reserveword.imblocker.common.accessor.AWCGGraphicsContextAccessor;

@Pseudo
@Mixin(value = CGGraphicsContext.class, remap = false)
public abstract class AWCGGraphicsContextMixin implements AWCGGraphicsContextAccessor {
	private float imblocker$scale = 1.0f;
	
	@Inject(method = "concatenateCTM", at = @At("TAIL"))
	public void updateScale(CGAffineTransform transform, CallbackInfo ci) {
		imblocker$scale *= transform.a;
	}
	
	@Override
	public float imblocker$getScale() {
		return imblocker$scale;
	}
}
