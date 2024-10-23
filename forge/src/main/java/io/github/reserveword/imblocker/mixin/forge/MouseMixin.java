package io.github.reserveword.imblocker.mixin.forge;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.Config;
import io.github.reserveword.imblocker.common.IMCheckState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

@Mixin(MouseHandler.class)
public abstract class MouseMixin {
    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "onPress", at = @At("HEAD"))
    private void checkScreenOnMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (minecraft.f_91080_ != null && !Config.INSTANCE.inScreenBlacklist(minecraft.f_91080_.getClass())) {
            IMCheckState.mouseEvent();
        }
    }
}