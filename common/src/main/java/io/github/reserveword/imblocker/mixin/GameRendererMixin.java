package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Inject(method = "extractGui", at = @At(value = "INVOKE", target = 
			"Lnet/minecraft/client/gui/Gui;extractDeferredSubtitles()V", shift = At.Shift.AFTER))
	public void renderUniversalPreeditOverlay(DeltaTracker deltaTracker, 
			boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo ci, 
			@Local GuiGraphicsExtractor graphics) {
		UniversalIMEPreeditOverlay.getInstance().renderOnMinecraftSurface(graphics);
	}
}
