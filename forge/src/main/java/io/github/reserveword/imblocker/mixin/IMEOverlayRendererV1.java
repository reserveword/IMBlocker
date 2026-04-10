package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftRenderApi;
import io.github.reserveword.imblocker.common.gui.UniversalEnglishStateIndicator;
import io.github.reserveword.imblocker.common.gui.UniversalIMECandidateOverlay;
import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;

/*Forge <1.20*/
@Mixin(Minecraft.class)
public abstract class IMEOverlayRendererV1 {
	@Inject(method = "runTick", at = @At(value = "CONSTANT", args = "stringValue=blit"))
	public void renderIMEOverlays(boolean tick, CallbackInfo ci) {
		if(FocusManager.getFocusedContainer() == FocusContainer.MINECRAFT) {
			PoseStack poseStack = new PoseStack();
			MinecraftRenderApi graphics = new MinecraftRenderApi() {
				@Override
				public void fillRect(int x1, int y1, int x2, int y2, int color) {
					GuiComponent.fill(poseStack, x1, y1, x2, y2, color);
				}
				
				@Override
				public void drawText(String text, int x, int y, int color) {
					Minecraft.getInstance().font.draw(poseStack, text, x, y, color);
				}
			};
			poseStack.translate(0.0D, 0.0D, 1000.0D);
			poseStack.pushPose();
			UniversalIMEPreeditOverlay.getInstance().renderOnMinecraftSurface(graphics);
			UniversalIMECandidateOverlay.getInstance().renderOnMinecraftSurface(graphics);
			UniversalEnglishStateIndicator.renderOnMinecraftSurface(graphics);
			poseStack.popPose();
		}
	}
}
