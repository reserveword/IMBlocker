package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = {
		"net.minecraft.client.gui.components.AbstractScrollWidget",
		"net.minecraft.client.gui.components.AbstractScrollArea"}, remap = false)
public abstract class AbstractScrollWidgetMixin extends AbstractWidgetMixin {
	
	@Shadow
	protected abstract double scrollAmount();
	
	@Inject(method = "setScrollAmount(D)V", at = @At("TAIL"))
	public void onScroll(double scroll, CallbackInfo ci) {}
}
