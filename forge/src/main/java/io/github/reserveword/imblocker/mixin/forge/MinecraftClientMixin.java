package io.github.reserveword.imblocker.mixin.forge;

import io.github.reserveword.imblocker.IMManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "setWindowActive", at = @At("HEAD"))
    public void syncIMState(CallbackInfo ci) {
        IMManager.syncState();
    }
}
