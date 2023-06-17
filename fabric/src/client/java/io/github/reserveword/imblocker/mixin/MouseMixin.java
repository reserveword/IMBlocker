package io.github.reserveword.imblocker.mixin;

import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Final
    @Shadow
    private final MinecraftClient client = null;

    @Inject(method = "onMouseButton(JIII)V", at = @At("HEAD"))
    private void checkScreenOnMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (client.currentScreen != null) {
            IMCheckState.mouseEvent();
        }
    }
//    @Inject(method = "method_1611([ZLnet/minecraft/client/gui/screen/Screen;DDI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseReleased(DDI)Z", shift = At.Shift.AFTER))
//    private static void afterMouseReleasedEvent(boolean[] resultHack, Screen screen, double mouseX, double mouseY, int button, CallbackInfo ci) {
//
//    }
//
//    // private synthetic method_1605([ZDDI)V
//    @Inject(method = "method_1605([ZLnet/minecraft/client/gui/screen/Screen;DDI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseReleased(DDI)Z", shift = At.Shift.AFTER))
//    private static void afterMouseReleasedEvent(boolean[] resultHack, Screen screen, double mouseX, double mouseY, int button, CallbackInfo ci) {
//
//    }
}