package io.github.reserveword.imblocker.mixin.forge;

import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.IMCheckState;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContainerEventHandler.class)
public interface ParentElementMixin {
    @Shadow
    @Nullable
    GuiEventListener getFocused();

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    default void captureChar(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        GuiEventListener focused = getFocused();
        if (focused != null && Common.classIsTextField(focused.getClass()) && IMCheckState.captureNonPrintable(focused, chr, true)) {
            cir.setReturnValue(false);
        }
    }
}
