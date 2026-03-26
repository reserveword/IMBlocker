package io.github.reserveword.imblocker.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {
	@Inject(method = "setPreeditOverlay", at = @At("HEAD"), cancellable = true)
	public void disableVanillaPreeditOverlay(Renderable preeditOverlay, CallbackInfo ci) {
		ci.cancel();
	}
	
	@Inject(method = "extractDeferredElements", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD,
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;preeditOverlay:Lnet/minecraft/client/gui/components/Renderable;"))
	public void renderEfficientPreeditOverlay(int mouseX, int mouseY, float a, CallbackInfo ci) {
		UniversalIMEPreeditOverlay.getInstance().renderOnMinecraftSurface((GuiGraphicsExtractor) (Object) this, mouseX, mouseY, a);
	}
}
