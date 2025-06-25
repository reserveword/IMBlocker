package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import net.minecraft.client.gui.widget.Widget;

@Mixin(Widget.class)
public abstract class AbstractWidgetMixin implements MinecraftFocusableWidget {

	@Shadow private int x;
	@Shadow private int y;
	@Shadow protected int width;
	@Shadow protected int height;
	
	@Inject(method = "setFocused", at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {}
	
	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(getGuiScale(), x, y, width, height);
	}
}
