package io.github.reserveword.imblocker.mixin.forge;

import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public abstract class TextFieldMixin {
    @Shadow
    public abstract boolean canConsumeInput();

    @Inject(method = {"tick", "renderButton"}, at = @At("HEAD"))
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
