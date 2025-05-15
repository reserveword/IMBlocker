package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;

@Pseudo
@Mixin(targets = "dev.ftb.mods.ftblibrary.ui.BaseScreen", remap = false)
public abstract class FtbBaseScreenMixin extends FtbWidgetMixin {
	
	@Shadow
	private MainWindow screen;
	
	@Inject(method = "getX", at = @At("HEAD"), cancellable = true)
	public void getX(CallbackInfoReturnable<Integer> cir) {
		if(screen == null && parent == null) {
			cir.setReturnValue((Minecraft.getInstance().getWindow().getGuiScaledWidth() - width) / 2);
		}
	}
	
	@Inject(method = "getY", at = @At("HEAD"), cancellable = true)
	public void getY(CallbackInfoReturnable<Integer> cir) {
		if(screen == null && parent == null) {
			cir.setReturnValue((Minecraft.getInstance().getWindow().getGuiScaledHeight() - height) / 2);
		}
	}
}
