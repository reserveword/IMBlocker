package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftRenderApi;
import io.github.reserveword.imblocker.common.gui.UniversalEnglishStateIndicator;
import io.github.reserveword.imblocker.common.gui.UniversalIMECandidateOverlay;
import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(MinecraftClient.class)
public abstract class IMEOverlayRendererV1 {
	@Inject(method = "render", at = @At(value = "CONSTANT", args = "stringValue=blit"))
	public void renderIMEOverlays(boolean tick, CallbackInfo ci) {
		if(FocusManager.getFocusedContainer() == FocusContainer.MINECRAFT) {
			MatrixStack modelViewStack = RenderSystem.getModelViewStack();
			modelViewStack.push();
			modelViewStack.loadIdentity();
			RenderSystem.applyModelViewMatrix();
			MatrixStack matrices = new MatrixStack();
			MinecraftRenderApi graphics = new MinecraftRenderApi() {
				@Override
				public void fillRect(int x1, int y1, int x2, int y2, int color) {
					DrawableHelper.fill(matrices, x1, y1, x2, y2, color);
				}
				
				@Override
				public void drawText(String text, int x, int y, int color) {
					MinecraftClient.getInstance().textRenderer.draw(matrices, text, x, y, color);
				}
			};
			matrices.push();
			matrices.translate(0.0D, 0.0D, -1000.0D);
			UniversalIMEPreeditOverlay.getInstance().renderOnMinecraftSurface(graphics);
			UniversalIMECandidateOverlay.getInstance().renderOnMinecraftSurface(graphics);
			UniversalEnglishStateIndicator.renderOnMinecraftSurface(graphics);
			matrices.pop();
			modelViewStack.pop();
			RenderSystem.applyModelViewMatrix();
		}
	}
}
