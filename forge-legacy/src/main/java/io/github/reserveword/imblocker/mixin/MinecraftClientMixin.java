package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
	
	@Shadow
	private MainWindow window;
	
	@Shadow
	private boolean noRender;
	
	@Unique
	private long lastGameRenderTime = 0;
	
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
    	if(IMBlockerConfig.INSTANCE.isScreenRecoveringEnabled() && screen != null) {
    		IMBlockerConfig.INSTANCE.recoverScreen(screen.getClass().getName());
    	}
    	
    	FocusContainer.MINECRAFT.clearFocus();
    	FocusContainer.MINECRAFT.setPreferredState(isScreenInWhiteList(screen));
    }
    
    @Inject(method = "runTick", at = @At("HEAD"))
	public void runPreRenderTasks(boolean tick, CallbackInfo ci) {
		IMBlockerCore.renderStart();
	}
	
    @ModifyConstant(method = "runTick", constant = @Constant(stringValue = "gameRenderer"))
	public String recordGameRenderStartTime(String location) {
		lastGameRenderTime = System.nanoTime();
		FocusManager.isGameRendering = true;
		return location;
	}
	
	@Inject(method = "runTick", at = @At(value = "INVOKE", target = 
			"Lnet/minecraft/profiler/IProfiler;pop()V"))
	public void captureGameRenderEnd(boolean tick, CallbackInfo ci) {
		if(FocusManager.isGameRendering) {
			FocusManager.isGameRendering = false;
			FocusContainer.MINECRAFT.checkFocusCandidatesVisibility(lastGameRenderTime);
		}
	}
    
    private boolean isScreenInWhiteList(Screen screen) {
    	return IMBlockerConfig.INSTANCE.isScreenInWhitelist(screen);
    }
}
