package io.github.reserveword.imblocker.common;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.sdl.SDLKeyboard;
import org.lwjgl.sdl.SDLProperties;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.sdl.SDL_Rect;
import org.lwjgl.sdl.SDL_Rect.Buffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;

public final class InputSystem {
	private static final InputSystemWrapper INSTANCE;
	
	public interface InputSystemWrapper{
		void setPreeditCursorRectangle(long window, int x, int y, int width, int height);
	}
	
	public static void setPreeditCursorRectangle(long window, int x, int y, int width, int height) {
		INSTANCE.setPreeditCursorRectangle(window, x, y, width, height);
	}
	
	static {
		if(MinecraftClientUtil.isGameVersionReached(777/*26.3*/)) {
			INSTANCE = new InputSystemWrapper() {
				@Override
				public void setPreeditCursorRectangle(long window, int x, int y, int width, int height) {
					try (MemoryStack stack = MemoryStack.stackPush()) {
						Buffer rect = SDL_Rect.malloc(1, stack).x(x).y(y).w(width).h(height);
						SDLKeyboard.SDL_SetTextInputArea(window, rect, -1);
					}
				}
			};
		}else {
			INSTANCE = new InputSystemWrapper() {
				@Override
				public void setPreeditCursorRectangle(long window, int x, int y, int width, int height) {
					GLFW.glfwSetPreeditCursorRectangle(window, x, y, width, height);
				}
			};
		}
	}
}
