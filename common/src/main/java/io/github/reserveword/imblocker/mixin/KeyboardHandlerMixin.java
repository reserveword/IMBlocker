package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.reserveword.imblocker.common.gui.EfficientIMEPreeditOverlay;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.PreeditEvent;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
	@Redirect(method = "preeditCallback", at = @At(value = "INVOKE", target = 
			"Lnet/minecraft/client/gui/screens/Screen;preeditUpdated(Lnet/minecraft/client/input/PreeditEvent;)Z"))
	public boolean redirectPreeditCallback(Screen screen, PreeditEvent preeditEvent) {
		EfficientIMEPreeditOverlay.getInstance().preeditContentUpdated(preeditEvent);
		return true;
	}
}
