package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftRenderApi;
import io.github.reserveword.imblocker.common.gui.UniversalEnglishStateIndicator;
import io.github.reserveword.imblocker.common.gui.UniversalIMECandidateOverlay;
import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;
import net.minecraft.class_332;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;

@Mixin(value = GameRenderer.class, remap = false)
public abstract class IMEOverlayRendererV2 {
	@Redirect(method = "method_3192", at = @At(value = "INVOKE", target = 
			"Lnet/minecraft/class_332;method_51452()V"))
	public void renderIMEOverlays(class_332 rawGraphics) {
		if(FocusManager.getFocusedContainer() == FocusContainer.MINECRAFT) {
			rawGraphics.method_51448().push();
			rawGraphics.method_51448().loadIdentity();
			rawGraphics.method_51448().translate(0.0D, 0.0D, 10000.0D);
			MinecraftRenderApi graphics = new MinecraftRenderApi() {
				@Override
				public void fillRect(int x1, int y1, int x2, int y2, int color) {
					rawGraphics.method_25294(x1, y1, x2, y2, color);
				}
				
				@Override
				public void drawText(String text, int x, int y, int color) {
					rawGraphics.method_51433(MinecraftClient.getInstance().textRenderer, text, x, y, color, false);
				}
			};
			imblocker$drawManaged(rawGraphics, () -> {
				UniversalIMEPreeditOverlay.getInstance().renderOnMinecraftSurface(graphics);
				UniversalIMECandidateOverlay.getInstance().renderOnMinecraftSurface(graphics);
				UniversalEnglishStateIndicator.renderOnMinecraftSurface(graphics);
			});
			rawGraphics.method_51448().pop();
		}
		rawGraphics.method_51452();
	}
	
	private void imblocker$drawManaged(class_332 drawContext, Runnable runnable) {
		if(!IMBlockerCore.isGameVersionReached(767/*1.21*/)) {
			drawContext.method_51741(runnable);
		}else {
			runnable.run();
		}
	}
}

