package io.github.reserveword.imblocker.mixin;

import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.windows.User32;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sun.jna.Platform;

import net.minecraft.client.util.Window;

@Mixin(value = Window.class, priority = 10001)
public class WindowMixin {
	
	@Shadow
	private long handle;
	
	@Shadow
	private boolean fullscreen;
	
	@Inject(method = "updateWindowRegion", at = @At(value = "INVOKE", target = 
			"Lorg/lwjgl/glfw/GLFW;glfwSetWindowMonitor(JJIIIII)V", shift = At.Shift.AFTER))
	public void tweakFullScreenWindowStyle(CallbackInfo ci) {
		if(Platform.isWindows()) {
			long hWnd = GLFWNativeWin32.glfwGetWin32Window(handle);
			if(fullscreen) {
				long style = User32.GetWindowLongPtr(hWnd, User32.GWL_STYLE) & 0x7FFFFFFF;
				int flags = User32.SWP_NOMOVE | User32.SWP_NOSIZE | User32.SWP_NOSENDCHANGING;
				User32.SetWindowLongPtr(hWnd, User32.GWL_STYLE, style);
				User32.SetWindowPos(hWnd, User32.HWND_NOTOPMOST, 0, 0, 0, 0, flags);
			}
		}
	}
}
