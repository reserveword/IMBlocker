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
		float getWindowPixelDensity();
		void setPreeditCursorRectangle(long window, int x, int y, int width, int height);
		WinDef.HWND getHWND(long window);
	}
	
	public static float getWindowPixelDensity() {
		return INSTANCE.getWindowPixelDensity();
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
				public float getWindowPixelDensity() {
					long window = Minecraft.getInstance().getWindow().handle();
					float pixelDensity = SDLVideo.SDL_GetWindowPixelDensity(window);
					return pixelDensity > 0 ? pixelDensity : 1.0f;
				}
				
				@Override
				public void setPreeditCursorRectangle(long window, int x, int y, int width, int height) {
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
				public float getWindowPixelDensity() {
					long window = Minecraft.getInstance().getWindow().handle();
					int[] windowWidth = new int[1], frameBufferWidth = new int[1];
					GLFW.glfwGetFramebufferSize(window, frameBufferWidth, new int[1]);
					GLFW.glfwGetWindowSize(window, windowWidth, new int[1]);
					return windowWidth[0] > 0 ? (float) frameBufferWidth[0] / (float) windowWidth[0] : 1.0f;
				}
				
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
