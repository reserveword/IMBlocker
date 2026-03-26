package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.Window;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;

@Mixin(Window.class)
public abstract class WindowMixin {
	@Shadow
	private long handle;
	
	@Inject(method = "onFocus", at = @At("TAIL"))
	public void onFocusChanged(long handle, boolean focused, CallbackInfo ci) {
		if(this.handle == handle) {
			FocusManager.setWindowFocused(focused);
		}
	}
	
	@Inject(method = "setGuiScale(I)V", at = @At("TAIL"))
	public void onScaleFactorChanged(int scaleFactor, CallbackInfo ci) {
		FocusContainer.MINECRAFT.setGuiScaleFactor(scaleFactor);
	}
}
