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
    private MinecraftClient client;

    @Inject(method = "onMouseButton(JIII)V", at = @At("HEAD"))
    private void checkScreenOnMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (client.currentScreen != null) {
            IMCheckState.mouseEvent();
        }
    }
}