package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.Window;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftScreenMonitor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
	
	@Shadow
	private Window window;
	
	@Unique
	private long lastGameRenderTime = 0;
	
	@Inject(method = "onTextInputFocusChange", at = @At("HEAD"), cancellable = true)
	public void disableVanillaFocusControl(GuiEventListener element, boolean isFocused, CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "setScreen", at = @At("HEAD"), require = 0)
	public void onScreenChanged(Screen screen, CallbackInfo ci) {
		MinecraftScreenMonitor.onScreenChanged(screen);
	}
	
	@Inject(method = "runTick", at = @At("HEAD"))
	public void runPreTickTasks(boolean tick, CallbackInfo ci) {
		IMBlockerCore.tickStart();
	}
	
	@Inject(method = "renderFrame", at = @At(value = "INVOKE", target = 
			"Lnet/minecraft/client/renderer/GameRenderer;extract(Lnet/minecraft/client/DeltaTracker;Z)V"))
	public void recordGameRenderStartTime(boolean tick, CallbackInfo ci) {
		lastGameRenderTime = System.nanoTime();
		FocusManager.isGameRendering = true;
	}
	
	@Inject(method = "renderFrame", at = @At(value = "INVOKE", target = 
			"Lnet/minecraft/client/renderer/GameRenderer;extract(Lnet/minecraft/client/DeltaTracker;Z)V", shift = At.Shift.AFTER))
	public void captureGameRenderEnd(boolean tick, CallbackInfo ci) {
		if(FocusManager.isGameRendering) {
			FocusManager.isGameRendering = false;
			FocusContainer.MINECRAFT.checkFocusCandidatesVisibility(lastGameRenderTime);
		}
	}
}
