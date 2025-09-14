package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManagerWindows;
import net.minecraft.client.MainWindow;

@Mixin(MainWindow.class)
public abstract class WindowsFullScreenPatch {
	
	@Shadow
	private boolean fullscreen;

	@Inject(method = "setMode", at = @At(value = "INVOKE", target = 
			"Lorg/lwjgl/glfw/GLFW;glfwSetWindowMonitor(JJIIIII)V", shift = At.Shift.AFTER))
	public void tweakFullScreenWindowStyle(CallbackInfo ci) {
		if(fullscreen) {
			IMBlockerCore.invokeOnMainThread(() -> IMManagerWindows.tweakFullScreenWindowStyle());
		}
	}
}
