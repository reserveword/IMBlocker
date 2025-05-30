package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.Config;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.GenericWhitelistScreen;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
	
	@Shadow
	private MainWindow window;
	
    @Inject(method = "setWindowActive", at = @At("HEAD"))
    public void onWindowFocusChanged(boolean isFocused, CallbackInfo ci) {
        FocusManager.setWindowFocused(isFocused);
    }
    
    @Inject(method = "resizeDisplay", at = @At("TAIL"))
    public void onResolutionChanged(CallbackInfo ci) {
    	FocusContainer.MINECRAFT.setGuiScaleFactor(window.getGuiScale());
    	IMManager.updateCompositionWindowPos();
    }
    
    @Inject(method = "setScreen", at = @At("HEAD"))
    public void onScreenChanged(Screen screen, CallbackInfo ci) {
    	if(Config.INSTANCE.isScreenRecoveringEnabled() && screen != null) {
    		Config.INSTANCE.recoverScreen(screen.getClass().getName());
    	}
    	
    	if(isScreenInWhiteList(screen)) {
    		FocusContainer.MINECRAFT.requestFocus(GenericWhitelistScreen.getInstance());
    	}else {
    		FocusContainer.MINECRAFT.cancelFocus();
    	}
    }
    
    private boolean isScreenInWhiteList(Screen screen) {
    	return Config.INSTANCE.isScreenInWhitelist(screen);
    }
}
