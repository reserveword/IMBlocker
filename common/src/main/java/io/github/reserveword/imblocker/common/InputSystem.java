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
import com.sun.jna.platform.win32.WinDef;

import net.minecraft.client.Minecraft;

public final class InputSystem {
	private static final InputSystemWrapper INSTANCE;
	
	public interface InputSystemWrapper {
		void setPreeditCursorRectangle(long window, int x, int y, int width, int height);
		WinDef.HWND getHWND(long window);
	}
	
	public static void setPreeditCursorRectangle(long window, double x, double y, double width, double height) {
		INSTANCE.setPreeditCursorRectangle(window, (int) x, (int) y, (int) width, (int) height);
	}
	
	public static void setPreeditCursorRectangle(long window, int x, int y, int width, int height) {
		INSTANCE.setPreeditCursorRectangle(window, x, y, width, height);
	}
	
	public static WinDef.HWND getHWND(long window) {
		return INSTANCE.getHWND(window);
	}
	
	static {
		if(IMBlockerCore.IS_SDL_PRESENT) {
			INSTANCE = new InputSystemWrapper() {
				@Override
				public void setPreeditCursorRectangle(long window, int x, int y, int width, int height) {
					float pixelDensity = SDLVideo.SDL_GetWindowPixelDensity(Minecraft.getInstance().getWindow().handle());
					if(pixelDensity > 0) {
						x = Math.round(x / pixelDensity);
						y = Math.round(y / pixelDensity);
						width = Math.round(width / pixelDensity);
						height = Math.round(height / pixelDensity);
					}
					
					try (MemoryStack stack = MemoryStack.stackPush()) {
						Buffer rect = SDL_Rect.malloc(1, stack).x(x).y(y).w(width).h(height);
						SDLKeyboard.SDL_SetTextInputArea(window, rect, -1);
					}
				}
				
				@Override
				public WinDef.HWND getHWND(long window) {
					return new WinDef.HWND(new Pointer(SDLProperties.SDL_GetPointerProperty(
							SDLVideo.SDL_GetWindowProperties(window), 
							"SDL.window.win32.hwnd",
							MemoryUtil.NULL)));
				}
			};
		}else {
			INSTANCE = new InputSystemWrapper() {
				@Override
				public void setPreeditCursorRectangle(long window, int x, int y, int width, int height) {
					GLFW.glfwSetPreeditCursorRectangle(window, x, y, width, height);
				}
				
				@Override
				public WinDef.HWND getHWND(long window) {
					return new WinDef.HWND(new Pointer(GLFWNativeWin32.glfwGetWin32Window(window)));
				}
			};
		}
	}
}
