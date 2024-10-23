package io.github.reserveword.imblocker.mixin.forge;

import io.github.reserveword.imblocker.Config;
import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseMixin {
    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "onPress", at = @At("HEAD"))
    private void checkScreenOnMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (minecraft.screen != null && !Config.INSTANCE.inScreenBlacklist(minecraft.screen.getClass())) {
            IMCheckState.mouseEvent();
        }
    }
}