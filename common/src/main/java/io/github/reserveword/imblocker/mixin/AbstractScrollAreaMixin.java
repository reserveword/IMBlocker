package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.components.AbstractScrollArea;

@Mixin(AbstractScrollArea.class)
public abstract class AbstractScrollAreaMixin extends AbstractWidgetMixin {
	
	@Shadow
	protected abstract double scrollAmount();
	
	@Inject(method = "setScrollAmount", at = @At("TAIL"))
	public void onScroll(double scroll, CallbackInfo ci) {}
}
