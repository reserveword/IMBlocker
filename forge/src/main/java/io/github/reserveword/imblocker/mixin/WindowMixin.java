package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.Window;

import io.github.reserveword.imblocker.common.gui.FocusContainer;

@Mixin(Window.class)
public abstract class WindowMixin {
	@Inject(method = "setGuiScale(D)V", at = @At("TAIL"), require = 0)
	public void onScaleFactorChanged(double scaleFactor, CallbackInfo ci) {
		FocusContainer.MINECRAFT.setGuiScaleFactor(scaleFactor);
	}
	
	@Inject(method = "setGuiScale(I)V", at = @At("TAIL"), require = 0, remap = false)
	public void onScaleFactorChanged(int scaleFactor, CallbackInfo ci) {
		FocusContainer.MINECRAFT.setGuiScaleFactor(scaleFactor);
	}
}
