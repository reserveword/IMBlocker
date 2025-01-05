package io.github.reserveword.imblocker.mixin.fabric;

import io.github.reserveword.imblocker.common.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.common.IMCheckState;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = WWidget.class, remap = false)
public abstract class CottonWidgetMixin implements FocusableWidgetAccessor {

    @Override
    public boolean isWidgetEditable() {
        return (getClass().equals(WTextField.class)) && ((WTextField) (Object) this).isEditable();
    }

    @Inject(method = "onFocusGained", at = @At("HEAD"))
    public void onFocusGained(CallbackInfo ci) {
        IMCheckState.focusGained(this);
    }

    @Inject(method = "onFocusLost", at = @At("HEAD"))
    public void onFocusLost(CallbackInfo ci) {
        IMCheckState.focusLost(this);
    }
}
