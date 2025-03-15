package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.GenericWhitelistScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	
    @Inject(method = "onWindowFocusChanged", at = @At("HEAD"))
    public void onWindowFocusChanged(boolean focused, CallbackInfo ci) {
    	FocusManager.setWindowFocused(focused);
    }
    
    @Inject(method = "setScreen", at = @At("HEAD"))
    public void onScreenChanged(Screen screen, CallbackInfo ci) {
    	if(isScreenInWhiteList(screen)) {
    		FocusContainer.MINECRAFT.requestFocus(GenericWhitelistScreen.getInstance());
    	}else {
    		FocusContainer.MINECRAFT.cancelFocus();
    	}
    }
    
    private boolean isScreenInWhiteList(Screen screen) {
//    	return screen != null && Config.INSTANCE.inScreenWhitelist(screen.getClass());
    	return screen instanceof AbstractSignEditScreen || screen instanceof BookEditScreen;
    }
}
