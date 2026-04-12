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
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_9779;

@Mixin(targets = "net.minecraft.class_757"/*net.minecraft.client.render.GameRenderer*/, remap = false)
public abstract class IMEOverlayRendererV3 {
	private class_332 imblocker$rawGraphics;
	
	@ModifyVariable(method = "method_3192", at = @At("STORE"))
	public class_332 captureGuiGraphics(class_332 rawGraphics) {
		imblocker$rawGraphics = rawGraphics;
		return rawGraphics;
	}
	
	@Inject(method = "method_3192", at = @At(value = "INVOKE", target = 
			"Lnet/minecraft/class_11228;method_70890(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"))
	public void renderIMEOverlays(class_9779 tracker, boolean tick, CallbackInfo ci) {
		if(FocusManager.getFocusedContainer() == FocusContainer.MINECRAFT) {
			MinecraftRenderApi graphics = new MinecraftRenderApi() {
				@Override
				public void fillRect(int x1, int y1, int x2, int y2, int color) {
					imblocker$rawGraphics.method_25294(x1, y1, x2, y2, color);
				}
				
				@Override
				public void drawText(String text, int x, int y, int color) {
					imblocker$rawGraphics.method_51433(class_310.method_1551().field_1772, text, x, y, color, false);
				}
			};
			UniversalIMEPreeditOverlay.getInstance().renderOnMinecraftSurface(graphics);
			UniversalIMECandidateOverlay.getInstance().renderOnMinecraftSurface(graphics);
			UniversalEnglishStateIndicator.renderOnMinecraftSurface(graphics);
		}
		imblocker$rawGraphics = null;
	}
}
