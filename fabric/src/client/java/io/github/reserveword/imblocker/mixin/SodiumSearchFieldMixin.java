package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;

@Pseudo
@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options."
		+ "client.gui.frame.components.SearchTextFieldComponent", remap = false)
public abstract class SodiumSearchFieldMixin implements MinecraftFocusableWidget {
	
	@Shadow
	protected boolean editable;
	
	@Override
	public boolean isWidgetEditable() {
		return editable; // Always true.
	}
	
	@Inject(method = {"setFocused", "method_25365"}, at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isFocused);
	}
}
