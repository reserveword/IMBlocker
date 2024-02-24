package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.Config;
import io.github.reserveword.imblocker.IMCheckState;
import io.github.reserveword.imblocker.IMManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "onWindowFocusChanged", at = @At("HEAD"))
    public void syncIMState(CallbackInfo ci) {
    	System.out.println("Window focus changed.");
//        IMManager.syncState();
    }
    
    @Inject(method = "setScreen", at = @At("HEAD"))
    public void onScreenClosed(Screen screen, CallbackInfo ci) {
    	if(isScreenInWhiteList(screen)) {
    		IMCheckState.isWhiteListScreenShowing = true;
    	}else {
    		IMCheckState.isWhiteListScreenShowing = false;
        	IMCheckState.focusedInputWidget = null;
    	}
    }
    
    private boolean isScreenInWhiteList(Screen screen) {
    	return screen != null && Config.INSTANCE.inScreenWhitelist(screen.getClass());
    }
}
