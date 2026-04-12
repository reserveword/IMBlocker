package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftRenderApi;
import io.github.reserveword.imblocker.common.gui.UniversalEnglishStateIndicator;
import io.github.reserveword.imblocker.common.gui.UniversalIMECandidateOverlay;
import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public abstract class IMEOverlayRendererV3 {
	private GuiGraphics imblocker$rawGraphics;
	
	@ModifyVariable(method = "render", at = @At("STORE"))
	public GuiGraphics captureGuiGraphics(GuiGraphics rawGraphics) {
		imblocker$rawGraphics = rawGraphics;
		return rawGraphics;
	}
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = 
			"Lnet/minecraft/client/gui/render/GuiRenderer;render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"))
	public void renderIMEOverlays(DeltaTracker tracker, boolean tick, CallbackInfo ci) {
		if(FocusManager.getFocusedContainer() == FocusContainer.MINECRAFT) {
			MinecraftRenderApi graphics = new MinecraftRenderApi() {
				@Override
				public void fillRect(int x1, int y1, int x2, int y2, int color) {
					imblocker$rawGraphics.fill(x1, y1, x2, y2, color);
				}
				
				@Override
				public void drawText(String text, int x, int y, int color) {
					imblocker$rawGraphics.drawString(Minecraft.getInstance().font, text, x, y, color, false);
				}
			};
			UniversalIMEPreeditOverlay.getInstance().renderOnMinecraftSurface(graphics);
			UniversalIMECandidateOverlay.getInstance().renderOnMinecraftSurface(graphics);
			UniversalEnglishStateIndicator.renderOnMinecraftSurface(graphics);
		}
		imblocker$rawGraphics = null;
	}
}
