package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Widget;
import io.github.reserveword.imblocker.common.ReflectionUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

@Mixin(value = BaseScreen.class, remap = false)
public abstract class FtbBaseScreenMixin extends FtbWidgetMixin {
	
	@Shadow
	private Window screen;
	
	@Inject(method = "getX", at = @At("HEAD"), cancellable = true)
	public void getX(CallbackInfoReturnable<Integer> cir) {
		if(screen == null && getParent() == null) {
			cir.setReturnValue((MinecraftClient.getInstance().getWindow().getScaledWidth() - getWidth()) / 2);
		}
	}
	
	@Inject(method = "getY", at = @At("HEAD"), cancellable = true)
	public void getY(CallbackInfoReturnable<Integer> cir) {
		if(screen == null && getParent() == null) {
			cir.setReturnValue((MinecraftClient.getInstance().getWindow().getScaledHeight() - getHeight()) / 2);
		}
	}
	
	private int getWidth() {
		return ReflectionUtil.getFieldValue(Widget.class, this, int.class, "width");
	}
	
	private int getHeight() {
		return ReflectionUtil.getFieldValue(Widget.class, this, int.class, "height");
	}
	
	private Panel getParent() {
		return ReflectionUtil.getFieldValue(Widget.class, this, Panel.class, "parent");
	}
}
