package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.GenericWhitelistScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	
	@Shadow
	private Window window;
	
    @Inject(method = "onWindowFocusChanged", at = @At("HEAD"))
    public void onWindowFocusChanged(boolean focused, CallbackInfo ci) {
    	FocusManager.setWindowFocused(focused);
    }
    
    @Inject(method = "onResolutionChanged", at = @At("TAIL"))
    public void onResolutionChanged(CallbackInfo ci) {
    	FocusContainer.MINECRAFT.setGuiScaleFactor(window.getScaleFactor());
    	IMManager.updateCompositionWindowPos();
    }
    
    @Inject(method = "setScreen", at = @At("HEAD"))
    public void onScreenChanged(Screen screen, CallbackInfo ci) {
    	if(IMBlockerConfig.INSTANCE.isScreenRecoveringEnabled() && screen != null) {
    		IMBlockerConfig.INSTANCE.recoverScreen(screen.getClass().getName());
    	}
    	
    	if(isScreenInWhiteList(screen)) {
    		FocusContainer.MINECRAFT.requestFocus(GenericWhitelistScreen.getInstance());
    	}else {
    		FocusContainer.MINECRAFT.cancelFocus();
    	}
    }
    
    private boolean isScreenInWhiteList(Screen screen) {
    	return IMBlockerConfig.INSTANCE.isScreenInWhitelist(screen);
    }
}
