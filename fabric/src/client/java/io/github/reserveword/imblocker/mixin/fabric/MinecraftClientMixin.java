package io.github.reserveword.imblocker.mixin.fabric;

import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.rules.ChatRule;
import io.github.reserveword.imblocker.rules.FocusRule;
import io.github.reserveword.imblocker.rules.ScreenListRule;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "onWindowFocusChanged", at = @At("HEAD"))
    public void syncIMState(CallbackInfo ci) {
        Common.LOGGER.info("Window focus changed.");
        IMManager.syncState();
    }

    @Inject(method = "setScreen", at = @At("HEAD"))
    public void onScreenChanged(Screen screen, CallbackInfo ci) {
        ScreenListRule.checkScreen(screen);
        FocusRule.focusedInputWidget = null;
        ChatRule.isChatScreenShowing = screen instanceof ChatScreen;
    }
}
