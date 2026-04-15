package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftRenderApi;
import io.github.reserveword.imblocker.common.gui.UniversalEnglishStateIndicator;
import io.github.reserveword.imblocker.common.gui.UniversalIMECandidateOverlay;
import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(value = GameRenderer.class, remap = false)
public abstract class IMEOverlayRendererV2 {
	@Redirect(method = "m_109093_", at = @At(value = "INVOKE", target = 
			"Lnet/minecraft/client/gui/GuiGraphics;m_280262_()V"))
	public void renderIMEOverlays(GuiGraphics rawGraphics) {
		if(FocusManager.getFocusedContainer() == FocusContainer.MINECRAFT) {
			rawGraphics.m_280168_().pushPose();
			rawGraphics.m_280168_().setIdentity();
			rawGraphics.m_280168_().translate(0.0D, 0.0D, 9000.0D);
			MinecraftRenderApi graphics = new MinecraftRenderApi() {
				@Override
				public void fillRect(int x1, int y1, int x2, int y2, int color) {
					rawGraphics.m_280509_(x1, y1, x2, y2, color);
				}
				
				@Override
				public void drawText(String text, int x, int y, int color) {
					rawGraphics.m_280056_(Minecraft.getInstance().font, text, x, y, color, false);
				}
			};
			rawGraphics.m_286007_(() -> {
				UniversalIMEPreeditOverlay.getInstance().renderOnMinecraftSurface(graphics);
				UniversalIMECandidateOverlay.getInstance().renderOnMinecraftSurface(graphics);
				UniversalEnglishStateIndicator.renderOnMinecraftSurface(graphics);
			});
			rawGraphics.m_280168_().popPose();
		}
		rawGraphics.m_280262_();
	}
}

