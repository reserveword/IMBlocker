package io.github.reserveword.imblocker.mixin.forge;

import dev.emi.emi.screen.widget.EmiSearchWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EmiSearchWidget.class)
public abstract class EmiSearchWidgetMixin extends TextFieldMixin {

    @Override
    @Inject(method = "setFocused", at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
        super.focusChanged(isFocused, ci);
    }
}
