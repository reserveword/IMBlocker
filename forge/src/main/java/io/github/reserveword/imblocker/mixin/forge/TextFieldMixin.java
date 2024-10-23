package io.github.reserveword.imblocker.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.IMCheckState;
import net.minecraft.client.gui.components.EditBox;

@Mixin(EditBox.class)
public abstract class TextFieldMixin {
    @Shadow
    public abstract boolean canConsumeInput();

    @Inject(method = {"tick", "renderWidget"}, at = @At("HEAD"))
    public void tickCallback(CallbackInfo ci) {
        IMCheckState.captureTick(this, this.canConsumeInput());
    }

    @Inject(method = "charTyped", at = @At("HEAD"))
    public void charTypedCallback(char codePoint, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        IMCheckState.captureNonPrintable(this, codePoint, this.canConsumeInput());
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    public void onClickCallback(double p_94125_, double p_94126_, int p_94127_, CallbackInfoReturnable<Boolean> cir) {
        IMCheckState.captureClick(this::canConsumeInput);
    }
}
