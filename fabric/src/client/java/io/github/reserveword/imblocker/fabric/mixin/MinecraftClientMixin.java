package io.github.reserveword.imblocker.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.Config;
import io.github.reserveword.imblocker.common.IMCheckState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Shadow
	public Screen currentScreen;
	
    @Inject(method = "onWindowFocusChanged", at = @At("HEAD"))
    public void syncIMState(CallbackInfo ci) {
    	System.out.println("Window focus changed.");
//        IMManager.syncState();
    }
    
    @Inject(method = "setScreen", at = @At("HEAD"))
    public void onScreenChanged(Screen screen, CallbackInfo ci) {
    	IMCheckState.isWhiteListScreenShowing = isScreenInWhiteList(screen);
    	IMCheckState.focusedInputWidget = null;
    	IMCheckState.isChatScreenShowing = screen instanceof ChatScreen;
    }
    
    private boolean isScreenInWhiteList(Screen screen) {
    	return screen != null && Config.INSTANCE.inScreenWhitelist(screen.getClass());
    }
}
