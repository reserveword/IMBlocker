package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.reserveword.imblocker.common.gui.EfficientIMEPreeditOverlay;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.PreeditEvent;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
	@Redirect(method = "submitPreeditEvent", at = @At(value = "INVOKE", target = 
			"Lnet/minecraft/client/gui/components/events/GuiEventListener;preeditUpdated(Lnet/minecraft/client/input/PreeditEvent;)Z"))
	private static boolean redirectPreeditCallback(GuiEventListener element, PreeditEvent preeditEvent) {
		EfficientIMEPreeditOverlay.getInstance().preeditContentUpdated(preeditEvent);
		return true;
	}
}
