package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.Window;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
	
	@Shadow
	private Window window;
	
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
		try {
			FocusContainer.MINECRAFT.setGuiScaleFactor(window.getGuiScale());
		} catch (NoSuchMethodError e) {
			FocusContainer.MINECRAFT.setGuiScaleFactor(ReflectionUtil
					.getFieldValue(window.getClass(), window, Number.class, "guiScale").doubleValue());
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
	
	@Inject(method = "runTick", at = @At("HEAD"))
	public void runPreRenderTasks(boolean tick, CallbackInfo ci) {
		IMBlockerCore.flushDeferredRunnables();
		if(!noRender) {
			lastGameRenderTime = System.nanoTime();
			FocusManager.isGameRendering = true;
		}
	}
	
	@Inject(method = "runTick", at = @At("TAIL"))
	public void checkFocusCandidatesVisibility(boolean tick, CallbackInfo ci) {
		FocusContainer.MINECRAFT.checkFocusCandidatesVisibility(lastGameRenderTime);
		FocusManager.isGameRendering = false;
	}

	private boolean isScreenInWhiteList(Screen screen) {
		return IMBlockerConfig.INSTANCE.isScreenInWhitelist(screen);
	}
}
