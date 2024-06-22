package io.github.reserveword.imblocker.mixin.fabric;

import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.IMCheckState;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParentElement.class)
public interface ParentElementMixin {
    @Shadow
    @Nullable
    Element getFocused();

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    default void captureChar(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        Element focused = getFocused();
        if (focused != null && Common.classIsTextField(focused.getClass()) && IMCheckState.captureNonPrintable(focused, chr, true)) {
            cir.setReturnValue(false);
        }
    }
}
