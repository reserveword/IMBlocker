package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import net.minecraft.client.util.Window;

@Mixin(value = Window.class, priority = 1)
public abstract class WindowMixin {
	@Inject(method = "setScaleFactor", at = @At("HEAD"), require = 0)
	public void onScaleFactorChanged(double scaleFactor, CallbackInfo ci) {
		FocusContainer.MINECRAFT.setGuiScaleFactor(scaleFactor);
	}
	
	@Inject(method = "method_15997(I)V", at = @At("HEAD"), require = 0, remap = false)
	public void onScaleFactorChanged(int scaleFactor, CallbackInfo ci) {
		FocusContainer.MINECRAFT.setGuiScaleFactor(scaleFactor);
	}
}
