package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.WindowsFullScreenManager;
import net.minecraft.client.MainWindow;

@Mixin(MainWindow.class)
public abstract class WindowsFullScreenPatch {
	
	@Shadow
	private long window;
	
	@Shadow
	private boolean fullscreen;

	@Inject(method = "setMode", at = @At(value = "INVOKE", target = 
			"Lorg/lwjgl/glfw/GLFW;glfwSetWindowMonitor(JJIIIII)V", shift = At.Shift.AFTER))
	public void tweakFullScreenWindowStyle(CallbackInfo ci) {
		if(fullscreen) {
			WindowsFullScreenManager.tweakFullScreenWindowStyle(window);
		}
	}
}
