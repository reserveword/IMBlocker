package io.github.reserveword.imblocker.mixin;

import com.june.notebook.screens.menuScreen;
import io.github.reserveword.imblocker.IMCheckState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(menuScreen.class)
public abstract class NotebookScreenMixin {
    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    public void charTypedCallback(char codePoint, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (IMCheckState.captureNonPrintable(this, codePoint, true)) {
            cir.setReturnValue(false);
        }
    }
}
