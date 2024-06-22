package io.github.reserveword.imblocker.mixin.forge;

import io.github.reserveword.imblocker.rules.ChatRule;
import io.github.reserveword.imblocker.rules.FocusRule;
import io.github.reserveword.imblocker.rules.ScreenListRule;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "setScreen", at = @At("HEAD"))
    public void onScreenChanged(Screen screen, CallbackInfo ci) {
        ScreenListRule.checkScreen(screen);
        FocusRule.focusedInputWidget = null;
        ChatRule.isChatScreenShowing = screen instanceof ChatScreen;
    }
}
