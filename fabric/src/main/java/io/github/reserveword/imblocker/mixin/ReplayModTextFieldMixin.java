package io.github.reserveword.imblocker.mixin;

import io.github.reserveword.imblocker.IMCheckState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = {
        "com.replaymod.lib.de.johni0702.minecraft.gui.element.AbstractGuiTextField"
}, remap = false)
public abstract class ReplayModTextFieldMixin {
    @Shadow
    public abstract boolean isFocused();

    @Inject(method = "writeChar*", at = @At("HEAD"))
    public void charTypedCallback(char c, CallbackInfoReturnable<Object> cir) {
        IMCheckState.captureNonPrintable(this, c, this.isFocused());
    }
}
