package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.emi.emi.screen.widget.EmiSearchWidget;
import io.github.reserveword.imblocker.common.IMCheckState;

@Mixin(value = EmiSearchWidget.class)
public abstract class EmiSearchWidgetMixin extends TextFieldMixin
{
	@Override
	@Inject(method = "setFocused", at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		if(isFocused) {
			IMCheckState.focusGained(this);
    	}else {
    		IMCheckState.focusLost(this);
    	}
	}
}
