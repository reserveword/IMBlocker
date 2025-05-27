package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.widget.ScrollableWidget;

@Mixin(ScrollableWidget.class)
public abstract class ScrollableWidgetMixin extends ClickableWidgetMixin {
	
	@Shadow
	protected abstract double getScrollY();
	
	@Inject(method = "setScrollY", at = @At("TAIL"))
	public void onScroll(double scrollY, CallbackInfo ci) {}
}
