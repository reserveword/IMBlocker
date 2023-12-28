package io.github.reserveword.imblocker.mixin;

import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = {
        "me.shedaniel.rei.impl.client.gui.widget.basewidgets.TextFieldWidget",
        "me.shedaniel.rei.gui.widget.TextFieldWidget"
}, remap = false)
public abstract class ReiTextFieldMixin {
    @Shadow
    protected boolean editable;

    @Shadow
    public abstract boolean isVisible();

    @Shadow
    public abstract boolean isFocused();

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickCallback(CallbackInfo ci) {
        boolean active = this.isVisible() && this.isFocused() && this.editable && MinecraftClient.getInstance().currentScreen != null;
        IMCheckState.captureTick(this, active);
    }

    @Inject(method = "charTyped", at = @At("HEAD"))
    public void charTypedCallback(char codePoint, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        IMCheckState.captureNonPrintable(this, codePoint, this.editable);
    }
}
