package io.github.reserveword.imblocker.mixin.fabric;

import io.github.reserveword.imblocker.common.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.rules.FocusRule;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {
        "com.replaymod.lib.de.johni0702.minecraft.gui.element.AbstractGuiTextField"
}, remap = false)
public abstract class ReplayModTextFieldMixin implements FocusableWidgetAccessor {

    @Override
    public boolean isWidgetEditable() {
        return true;
    }

    @Inject(method = "onFocusChanged", at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
        FocusRule.focusChanged(this, isFocused);
    }
}
