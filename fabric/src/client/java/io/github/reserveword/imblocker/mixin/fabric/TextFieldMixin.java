package io.github.reserveword.imblocker.mixin.fabric;

import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldMixin {
    @Shadow
    public abstract boolean isActive();

    @Inject(method = {"tick", "renderButton"}, at = @At("HEAD"))
    public void tickCallback(CallbackInfo ci) {
        IMCheckState.captureTick(this, this.isActive());
    }

    @Inject(method = "charTyped", at = @At("HEAD"))
    public void charTypedCallback(char codePoint, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        IMCheckState.captureNonPrintable(this, codePoint, this.isActive());
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    public void onClickCallback(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        IMCheckState.captureClick(this::isActive);
    }
}
