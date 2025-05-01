package io.github.reserveword.imblocker.mixin;

import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.windows.User32;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.Window;
import com.sun.jna.Platform;

@Mixin(Window.class)
public class WindowMixin {

	@Shadow
	private long window;
	
	@Shadow
	private boolean fullscreen;

	@Inject(method = "setMode", at = @At(value = "INVOKE", target = 
			"Lorg/lwjgl/glfw/GLFW;glfwSetWindowMonitor(JJIIIII)V", shift = At.Shift.AFTER))
	public void tweakFullScreenWindowStyle(CallbackInfo ci) {
		if(Platform.isWindows()) {
			long hWnd = GLFWNativeWin32.glfwGetWin32Window(window);
			if(fullscreen) {
				long style = User32.WS_VISIBLE | User32.WS_CLIPSIBLINGS | User32.WS_CLIPCHILDREN;
				int flags = User32.SWP_NOMOVE | User32.SWP_NOSIZE | User32.SWP_NOSENDCHANGING;
				User32.SetWindowLongPtr(hWnd, User32.GWL_STYLE, style);
				User32.SetWindowPos(hWnd, User32.HWND_NOTOPMOST, 0, 0, 0, 0, flags);
			}
		}
	}
}
