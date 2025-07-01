package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

	@Shadow
	private Window window;
	
	@Shadow
	private boolean skipGameRender;
	
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
	public void runDeferredRunnables(boolean tick, CallbackInfo ci) {
		IMBlockerCore.flushDeferredRunnables();
		if(!skipGameRender) {
			lastGameRenderTime = System.nanoTime();
			FocusManager.isGameRendering = true;
		}
	}
	
	@Inject(method = "render", at = @At("TAIL"))
	public void checkFocusCandidatesVisibility(boolean tick, CallbackInfo ci) {
		FocusContainer.MINECRAFT.getFocusCandidates().forEach(focusCandidate -> {
			if(focusCandidate instanceof MinecraftTextFieldWidget textFieldFocusCandidate) {
				textFieldFocusCandidate.checkVisibility(lastGameRenderTime);
			}
		});
		FocusManager.isGameRendering = false;
	}

	private boolean isScreenInWhiteList(Screen screen) {
		return IMBlockerConfig.INSTANCE.isScreenInWhitelist(screen);
	}
}
