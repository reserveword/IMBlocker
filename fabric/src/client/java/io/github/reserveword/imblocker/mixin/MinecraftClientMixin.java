package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftScreenMonitor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

	@Shadow
	private Window window;
	
	@Unique
	private long lastGameRenderTime = 0;

	@Inject(method = "onWindowFocusChanged", at = @At("HEAD"))
	public void onWindowFocusChanged(boolean focused, CallbackInfo ci) {
		FocusManager.setWindowFocused(focused);
	}

	@Inject(method = "setScreen", at = @At("HEAD"))
	public void onScreenChanged(Screen screen, CallbackInfo ci) {
		MinecraftScreenMonitor.onScreenChanged(screen);
	}
	
	@Inject(method = "render", at = @At("HEAD"))
	public void runPreRenderTasks(boolean tick, CallbackInfo ci) {
		IMBlockerCore.renderStart();
	}
	
	@Inject(method = "render", at = @At(value = "CONSTANT", args = "stringValue=gameRenderer"))
	public void recordGameRenderStartTime(boolean tick, CallbackInfo ci) {
		lastGameRenderTime = System.nanoTime();
		FocusManager.isGameRendering = true;
	}
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = 
			"Lnet/minecraft/util/profiler/Profiler;pop()V"))
	public void captureGameRenderEnd(boolean tick, CallbackInfo ci) {
		if(FocusManager.isGameRendering) {
			FocusManager.isGameRendering = false;
			FocusContainer.MINECRAFT.checkFocusCandidatesVisibility(lastGameRenderTime);
		}
	}
}
