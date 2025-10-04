package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientUtil;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Pseudo
@Mixin(value = WWidget.class, remap = false)
public abstract class LibGuiWidgetMixin implements MinecraftFocusableWidget {
	
	@Shadow protected abstract int getAbsoluteX();
	@Shadow protected abstract int getAbsoluteY();
	@Shadow protected int width;
	@Shadow protected int height;

	@Inject(method = "onFocusLost", at = @At("TAIL"))
	public void onFocusLost(CallbackInfo ci) {}

	@Inject(method = "setLocation", at = @At("TAIL"))
	public void handleLocationChanged(int x, int y, CallbackInfo ci) {}

	@Inject(method = "setSize", at = @At("TAIL"))
	public void handleSizeChanged(int width, int height, CallbackInfo ci) {}

	@Override
	public Rectangle getBoundsAbs() {
		int x = getAbsoluteX();
		int y = getAbsoluteY();
		Object currentScreen = MinecraftClientUtil.getCurrentScreen();
		if (CottonClientScreen.class.isInstance(currentScreen)) {
			x += ReflectionUtil.getFieldValue(CottonClientScreen.class, currentScreen, int.class, "left");
			y += ReflectionUtil.getFieldValue(CottonClientScreen.class, currentScreen, int.class, "top");
		}
		return new Rectangle(getGuiScale(), x, y, width, height);
	}
}
