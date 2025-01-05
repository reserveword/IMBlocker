package io.github.reserveword.imblocker.mixin.fabric;

import io.github.reserveword.imblocker.common.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.common.IMCheckState;

import dev.ftb.mods.ftblibrary.ui.TextBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TextBox.class, remap = false)
public abstract class FtbTextFieldMixin implements FocusableWidgetAccessor {
    @Override
    public boolean isWidgetEditable() {
        return true;
    }

    @Inject(method = "setFocused", at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
        IMCheckState.focusChanged(this, isFocused);
    }
}
