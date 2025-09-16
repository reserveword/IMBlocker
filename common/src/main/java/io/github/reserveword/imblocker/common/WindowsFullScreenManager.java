package io.github.reserveword.imblocker.common;

import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.windows.User32;

public class WindowsFullScreenManager {
	
	private static final int setWindowLayerFlags = User32.SWP_NOMOVE | User32.SWP_NOSIZE | User32.SWP_NOSENDCHANGING;

	public static void tweakFullScreenWindowStyle(long handle) {
		IMBlockerCore.invokeOnMainThread(() -> {
			long hWnd = GLFWNativeWin32.glfwGetWin32Window(handle);
			long style = User32.GetWindowLongPtr(hWnd, User32.GWL_STYLE) & 0x7FFFFFFF;
			User32.SetWindowLongPtr(hWnd, User32.GWL_STYLE, style);
			User32.SetWindowPos(hWnd, User32.HWND_NOTOPMOST, 0, 0, 0, 0, setWindowLayerFlags);
		});
	}
}
