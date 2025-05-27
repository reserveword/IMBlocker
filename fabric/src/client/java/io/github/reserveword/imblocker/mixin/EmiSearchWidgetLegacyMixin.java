package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.emi.emi.screen.widget.EmiSearchWidget;

@Mixin(value = EmiSearchWidget.class)
public abstract class EmiSearchWidgetLegacyMixin extends TextFieldLegacyMixin {
	
	@Override
    @Inject(method = {"setFocused", "method_25365"}, at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
        super.focusChanged(isFocused, ci);
    }
	
	@Override
	public void focusBeChanged(boolean isFocused, CallbackInfo ci) {}
}
