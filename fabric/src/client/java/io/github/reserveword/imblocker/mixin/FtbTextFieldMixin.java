package io.github.reserveword.imblocker.mixin;

import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.input.KeyModifiers;
import io.github.reserveword.imblocker.IMCheckState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TextBox.class, remap = false)
public abstract class FtbTextFieldMixin {
    @Shadow
    public abstract boolean isFocused();

    @Inject(method = "charTyped", at = @At("HEAD"))
    public void charTypedCallback(char c, KeyModifiers modifiers, CallbackInfoReturnable<Boolean> cir) {
        IMCheckState.captureNonPrintable(this, c, this.isFocused());
    }
}
