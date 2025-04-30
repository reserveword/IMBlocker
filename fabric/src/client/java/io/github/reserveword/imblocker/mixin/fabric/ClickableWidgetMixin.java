package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import net.minecraft.client.gui.widget.ClickableWidget;

@Mixin(ClickableWidget.class)
public abstract class ClickableWidgetMixin implements MinecraftFocusableWidget {
	
	@Shadow private int x;
	@Shadow private int y;
	@Shadow protected int width;
	@Shadow protected int height;
	
	@Inject(method = {"setFocused", "method_25365"}, at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {}
	
	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(FocusContainer.getMCGuiScaleFactor(), x, y, width, height);
	}
}
