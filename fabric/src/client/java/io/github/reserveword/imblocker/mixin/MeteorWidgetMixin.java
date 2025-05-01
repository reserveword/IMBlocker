package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Pseudo
@Mixin(targets = "meteordevelopment.meteorclient.gui.widgets.WWidget", remap = false)
public abstract class MeteorWidgetMixin implements MinecraftFocusableWidget {
	
	@Shadow double x;
	@Shadow double y;
	@Shadow double width;
	@Shadow double height;
	
	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle((int) x, (int) y, (int) width, (int) height);
	}
	
	@Inject(method = "calculateWidgetPositions", at = @At("TAIL"))
	public void onLayoutWidget(CallbackInfo ci) {}
}
