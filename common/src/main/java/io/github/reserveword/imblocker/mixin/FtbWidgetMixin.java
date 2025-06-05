package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Widget;
import io.github.reserveword.imblocker.common.IMManager;
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
    	IMManager.updateCompositionWindowPos();
    }
	
	@Inject(method = "setY", at = @At("TAIL"))
	public void handleYChanged(int y, CallbackInfo ci) {
    	IMManager.updateCompositionWindowPos();
    }
	
	@Inject(method = "setWidth", at = @At("TAIL"))
	public void handleWidthChanged(int x, CallbackInfo ci) {
    	IMManager.updateCompositionWindowPos();
    }
	
	@Inject(method = "setHeight", at = @At("TAIL"))
	public void handleHeightChanged(int y, CallbackInfo ci) {
    	IMManager.updateCompositionWindowPos();
    }
	
	@Unique
	public int getAbsoluteX() {
		int x = posX;
		Panel p = parent;
		while(p != null) {
			x += p.posX;
			p = ((FtbWidgetMixin) (Object) p).parent;
		}
		return x;
	}
	
	@Unique
	public int getAbsoluteY() {
		int y = posY;
		Panel p = parent;
		while(p != null) {
			y += p.posY;
			p = ((FtbWidgetMixin) (Object) p).parent;
		}
		return y;
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(getGuiScale(), getAbsoluteX(), getAbsoluteY(), width, height);
	}
}
