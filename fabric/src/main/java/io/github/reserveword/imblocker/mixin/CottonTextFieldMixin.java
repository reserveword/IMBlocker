package io.github.reserveword.imblocker.mixin;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.reserveword.imblocker.IMCheckState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = WTextField.class, remap = false)
public abstract class CottonTextFieldMixin {
    @Unique
    protected boolean editable;

    @Inject(method = "onCharTyped", at = @At("HEAD"), cancellable = true)
    public void charTypedCallback(char ch, CallbackInfo cir) {
        if (IMCheckState.captureNonPrintable(this, ch, this.editable)) {
            cir.cancel();
        }
    }
}
