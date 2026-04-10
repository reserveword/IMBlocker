package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftRenderApi;
import io.github.reserveword.imblocker.common.gui.UniversalEnglishStateIndicator;
import io.github.reserveword.imblocker.common.gui.UniversalIMECandidateOverlay;
import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
	
	@Shadow
	private MainWindow window;
	
	@Shadow
	public FontRenderer font;
	
	@Unique
	private long lastGameRenderTime = 0;
	
    @Inject(method = "setWindowActive", at = @At("HEAD"))
    public void onWindowFocusChanged(boolean isFocused, CallbackInfo ci) {
        FocusManager.setWindowFocused(isFocused);
    }
    
    @Inject(method = "setScreen", at = @At("HEAD"))
    public void onScreenChanged(Screen screen, CallbackInfo ci) {
    	if(IMBlockerConfig.INSTANCE.isScreenRecoveringEnabled() && screen != null) {
    		IMBlockerConfig.INSTANCE.recoverScreen(screen.getClass().getName());
    	}
    	
    	if(!IMBlockerCore.isFTBScreen(screen)) {
    		FocusContainer.MINECRAFT.clearFocus();
    	}
    	FocusContainer.MINECRAFT.setPreferredState(isScreenInWhiteList(screen));
    }
    
    @Inject(method = "runTick", at = @At("HEAD"))
	public void runPreRenderTasks(boolean tick, CallbackInfo ci) {
		IMBlockerCore.renderStart();
	}
	
    @Inject(method = "runTick", at = @At(value = "CONSTANT", args = "stringValue=gameRenderer"))
	public void recordGameRenderStartTime(boolean tick, CallbackInfo ci) {
		lastGameRenderTime = System.nanoTime();
		FocusManager.isGameRendering = true;
	}
	
	@Inject(method = "runTick", at = @At(value = "INVOKE", target = 
			"Lnet/minecraft/profiler/IProfiler;pop()V"))
	public void captureGameRenderEnd(boolean tick, CallbackInfo ci) {
		if(FocusManager.isGameRendering) {
			FocusManager.isGameRendering = false;
			FocusContainer.MINECRAFT.checkFocusCandidatesVisibility(lastGameRenderTime);
		}
	}
	
	@Inject(method = "runTick", at = @At(value = "CONSTANT", args = "stringValue=blit"))
	public void renderIMEOverlays(boolean tick, CallbackInfo ci) {
		if(FocusManager.getFocusedContainer() == FocusContainer.MINECRAFT) {
			MatrixStack matrixStack = new MatrixStack();
			MinecraftRenderApi graphics = new MinecraftRenderApi() {
				@Override
				public void fillRect(int x1, int y1, int x2, int y2, int color) {
					AbstractGui.fill(matrixStack, x1, y1, x2, y2, color);
				}
				
				@Override
				public void drawText(String text, int x, int y, int color) {
					font.draw(matrixStack, text, x, y, color);
				}
			};
			matrixStack.translate(0.0D, 0.0D, 1000.0D);
			matrixStack.pushPose();
			UniversalIMEPreeditOverlay.getInstance().renderOnMinecraftSurface(graphics);
			UniversalIMECandidateOverlay.getInstance().renderOnMinecraftSurface(graphics);
			UniversalEnglishStateIndicator.renderOnMinecraftSurface(graphics);
			matrixStack.popPose();
		}
	}
    
    private boolean isScreenInWhiteList(Screen screen) {
    	return IMBlockerConfig.INSTANCE.isScreenInWhitelist(screen);
    }
}
