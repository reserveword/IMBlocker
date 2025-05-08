package io.github.reserveword.imblocker.mixin;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Widget;
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
		try {
			Field widthField = Widget.class.getDeclaredField("width");
			widthField.setAccessible(true);
			return widthField.getInt(this);
		} catch (Exception e) {
			return 0;
		}
	}
	
	private int getHeight() {
		try {
			Field widthField = Widget.class.getDeclaredField("height");
			widthField.setAccessible(true);
			return widthField.getInt(this);
		} catch (Exception e) {
			return 0;
		}
	}
	
	private Panel getParent() {
		try {
			Field parentField = Widget.class.getDeclaredField("parent");
			parentField.setAccessible(true);
			return (Panel) parentField.get(this);
		} catch (Exception e) {
			return null;
		}
	}
}
