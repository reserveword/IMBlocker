package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftblibrary.ui.Widget;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Mixin(value = Widget.class, remap = false)
public abstract class FtbWidgetMixin implements MinecraftFocusableWidget {
	
	@Shadow public abstract int getX();
	@Shadow public abstract int getY();
	@Shadow public int width;
	@Shadow public int height;
	
	@Inject(method = "onClosed", at = @At("TAIL"))
	public void cancelFocus(CallbackInfo ci) {}

	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(FocusContainer.getMCGuiScaleFactor(), getX(), getY(), width, height);
	}
	
	@Inject(method = "setX", at = @At("TAIL"))
	public void handleXChanged(int x, CallbackInfo ci) {
    	IMManager.updateCompositionWindowPos();
    }
	
	@Inject(method = "setY", at = @At("TAIL"))
	public void handleYChanged(int y, CallbackInfo ci) {
    	IMManager.updateCompositionWindowPos();
    }
}
