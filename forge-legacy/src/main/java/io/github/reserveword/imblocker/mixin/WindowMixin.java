package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import net.minecraft.client.MainWindow;

@Mixin(MainWindow.class)
public abstract class WindowMixin {
	@Inject(method = "setGuiScale", at = @At("TAIL"))
	public void onScaleFactorChanged(double scaleFactor, CallbackInfo ci) {
		FocusContainer.MINECRAFT.setGuiScaleFactor(scaleFactor);
	}
}
