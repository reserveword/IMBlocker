package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
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
	@Redirect(method = "render", at = @At(value = "INVOKE", target = 
			"Lnet/minecraft/client/gui/GuiGraphics;flush()V"))
	public void renderIMEOverlaysUnobf(GuiGraphics rawGraphics) {
		if(IMBlockerConfig.INSTANCE.isIngameIMEEnabled() &&
				FocusManager.getFocusedContainer() == FocusContainer.MINECRAFT) {
			GuiGraphics privateGraphics = new GuiGraphics(Minecraft.getInstance(), 
					Minecraft.getInstance().renderBuffers().bufferSource());
			privateGraphics.pose().pushPose();
			privateGraphics.pose().setIdentity();
			privateGraphics.pose().translate(0.0D, 0.0D, 10000.0D);
			MinecraftRenderApi graphics = new MinecraftRenderApi() {
				@Override
				public void fillRect(int x1, int y1, int x2, int y2, int color) {
					privateGraphics.fill(x1, y1, x2, y2, color);
				}
				
				@Override
				public void drawText(String text, int x, int y, int color) {
					privateGraphics.drawString(Minecraft.getInstance().font, text, x, y, color, false);
				}
			};
			UniversalIMEPreeditOverlay.getInstance().renderOnMinecraftSurface(graphics);
			UniversalIMECandidateOverlay.getInstance().renderOnMinecraftSurface(graphics);
			UniversalEnglishStateIndicator.renderOnMinecraftSurface(graphics);
			privateGraphics.pose().popPose();
		}
		rawGraphics.flush();
	}
}

