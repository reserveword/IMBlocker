package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options.client.gui.widget.BaseWidget", remap = false)
public abstract class RSOBaseWidgetMixin implements MinecraftFocusableWidget {
	@Shadow public abstract int getX();
	@Shadow public abstract int getY();
	@Shadow public abstract int getWidth();
	@Shadow public abstract int getHeight();
	
	@Inject(method = { "setFocused", "method_25365" }, at = @At("TAIL"))
	public void focusChanged(boolean focused, CallbackInfo ci) {}
	
	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(getGuiScale(), getX(), getY(), getWidth(), getHeight());
	}
}
