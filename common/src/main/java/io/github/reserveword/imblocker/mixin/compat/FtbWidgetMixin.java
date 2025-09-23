package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Widget;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Mixin(value = Widget.class, remap = false)
public abstract class FtbWidgetMixin implements MinecraftFocusableWidget {
	
	@Shadow
	protected Panel parent;
	
	@Shadow public int posX;
	@Shadow public int posY;
	@Shadow public int width;
	@Shadow public int height;
	
	@Inject(method = "onClosed", at = @At("TAIL"))
	public void cancelFocus(CallbackInfo ci) {}
	
	@Inject(method = "setX", at = @At("TAIL"))
	public void handleXChanged(int x, CallbackInfo ci) {
		handleBoundsChanged();
	}
	
	@Inject(method = "setY", at = @At("TAIL"))
	public void handleYChanged(int y, CallbackInfo ci) {
		handleBoundsChanged();
	}
	
	@Inject(method = "setWidth", at = @At("TAIL"))
	public void handleWidthChanged(int x, CallbackInfo ci) {
		handleBoundsChanged();
	}
	
	@Inject(method = "setHeight", at = @At("TAIL"))
	public void handleHeightChanged(int y, CallbackInfo ci) {
		handleBoundsChanged();
	}
	
	public void handleBoundsChanged() {}
	
	@Unique
	public int getAbsoluteX() {
		int x = posX;
		for(Panel p = parent; p != null; p = ((FtbWidgetMixin) (Object) p).parent) {
			x += p.posX;
		}
		return x;
	}
	
	@Unique
	public int getAbsoluteY() {
		int y = posY;
		for(Panel p = parent; p != null; p = ((FtbWidgetMixin) (Object) p).parent) {
			y += p.posY;
		}
		return y;
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(getGuiScale(), getAbsoluteX(), getAbsoluteY(), width, height);
	}
}
