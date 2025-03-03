package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import net.minecraft.client.gui.widget.ClickableWidget;

@Mixin(ClickableWidget.class)
public abstract class ClickableWidgetMixin implements MinecraftFocusableWidget {
	
	@Override
	public boolean isWidgetEditable() {
		return false;
	}
	
	@Inject(method = {"setFocused", "method_25365"}, at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
    	onFocusChanged(isFocused);
    }
}
