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
import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
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

	@Inject(method = "onResolutionChanged", at = @At("TAIL"))
	public void onResolutionChanged(CallbackInfo ci) {
		try {
			FocusContainer.MINECRAFT.setGuiScaleFactor(window.getScaleFactor());
		} catch (NoSuchMethodError e) {
			FocusContainer.MINECRAFT.setGuiScaleFactor(ReflectionUtil
					.getFieldValue(window.getClass(), window, Number.class, "field_5179").doubleValue());
		}
		IMManager.updateCompositionWindowPos();
		IMManager.updateCompositionFontSize();
	}

	@Inject(method = "setScreen", at = @At("HEAD"))
	public void onScreenChanged(Screen screen, CallbackInfo ci) {
		if(IMBlockerConfig.INSTANCE.isScreenRecoveringEnabled() && screen != null) {
			IMBlockerConfig.INSTANCE.recoverScreen(screen.getClass().getName());
		}

		FocusContainer.MINECRAFT.clearFocus();
		FocusContainer.MINECRAFT.setPreferredState(isScreenInWhiteList(screen));
	}
	
	@Inject(method = "render", at = @At("HEAD"))
	public void runPreRenderTasks(boolean tick, CallbackInfo ci) {
		IMBlockerCore.renderStart();
	}
	
	@ModifyConstant(method = "render", constant = @Constant(stringValue = "gameRenderer"))
	public String recordGameRenderStartTime(String location) {
		lastGameRenderTime = System.nanoTime();
		FocusManager.isGameRendering = true;
		return location;
	}
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = 
			"Lnet/minecraft/util/profiler/Profiler;pop()V"))
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
