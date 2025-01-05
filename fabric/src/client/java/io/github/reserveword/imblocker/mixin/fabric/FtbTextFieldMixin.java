package io.github.reserveword.imblocker.mixin.fabric;

import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.input.KeyModifiers;
import io.github.reserveword.imblocker.common.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.common.IMCheckState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TextBox.class, remap = false)
public abstract class FtbTextFieldMixin implements FocusableWidgetAccessor {
    @Shadow
    public abstract boolean isFocused();
    
    @Override
    public boolean isWidgetEditable() {
    	return true;
    }

    /*
    @Inject(method = "charTyped", at = @At("HEAD"))
    public void charTypedCallback(char c, KeyModifiers modifiers, CallbackInfoReturnable<Boolean> cir) {
        IMCheckState.captureNonPrintable(this, c, this.isFocused());
    }
    */
    
    @Inject(method = "setFocused", at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
    	IMCheckState.focusChanged(this, isFocused);
    }
}
