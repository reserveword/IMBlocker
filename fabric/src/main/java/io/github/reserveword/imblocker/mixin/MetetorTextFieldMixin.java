package io.github.reserveword.imblocker.mixin;

import io.github.reserveword.imblocker.IMCheckState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "meteordevelopment.meteorclient.gui.widgets.input.WTextBox", remap = false)
@Pseudo
public abstract class MetetorTextFieldMixin {
    @Shadow
    protected boolean focused;

    @Inject(method = "onCharTyped", at = @At("HEAD"), cancellable = true)
    public void charTypedCallback(char c, CallbackInfoReturnable<Boolean> cir) {
        if (IMCheckState.captureNonPrintable(this, c, this.focused)) {
            cir.setReturnValue(false);
        }
    }
}
