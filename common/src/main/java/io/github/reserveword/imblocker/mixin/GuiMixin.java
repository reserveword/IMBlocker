package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftScreenMonitor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;

@Mixin(Gui.class)
public class GuiMixin {
	@Inject(method = "setScreen", at = @At("HEAD"), require = 0)
	public void onScreenChanged(Screen screen, CallbackInfo ci) {
		//26.2+
		MinecraftScreenMonitor.onScreenChanged(screen);
	}
}
