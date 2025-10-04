package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.Window;

import io.github.reserveword.imblocker.common.WindowsFullScreenManager;

@Mixin(value = Window.class, priority = 10001)
public abstract class WindowsFullScreenPatch {
	
	@Shadow
	private long handle;
	
	@Shadow
	private boolean fullscreen;

	@Inject(method = "setMode", at = @At(value = "INVOKE", target = 
			"Lorg/lwjgl/glfw/GLFW;glfwSetWindowMonitor(JJIIIII)V", shift = At.Shift.AFTER))
	public void tweakFullScreenWindowStyle(CallbackInfo ci) {
		if(fullscreen) {
			WindowsFullScreenManager.tweakFullScreenWindowStyle(handle);
		}
	}
}
