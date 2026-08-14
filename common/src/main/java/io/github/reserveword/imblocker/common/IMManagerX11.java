package io.github.reserveword.imblocker.common;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.glfw.GLFWNativeX11;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

final class IMManagerX11 extends IMManagerLinux {
	public interface Xlib extends Library {
		@SuppressWarnings("deprecation")
		Xlib INSTANCE = Native.loadLibrary("X11", Xlib.class);
		
		void XSetICFocus(Pointer ic);
		void XUnsetICFocus(Pointer ic);
		void XFlush(Pointer display);
	}

	private final long glfwWindow;
	private final long x11Window;
	private final Pointer display;
	private final Pointer xic;
	
	public IMManagerX11(long glfwWindow, long x11Window) throws RuntimeException {
		this.glfwWindow = glfwWindow;
		this.x11Window = x11Window;
		this.display = new Pointer(GLFWNativeX11.glfwGetX11Display());
		this.xic = retrieveXIC();
		if(xic == null) {
			throw new RuntimeException("[IMBlocker] Failed to retrieve XIC, fallback to basic IMManager.");
		}
	}
	
	@Override
	public void setState(boolean on) {
		if(on) {
			Xlib.INSTANCE.XSetICFocus(xic);
		}else {
			Xlib.INSTANCE.XUnsetICFocus(xic);
		}
		Xlib.INSTANCE.XFlush(display);
	}
	
	private Pointer retrieveXIC() {
		Pointer base = new Pointer(glfwWindow);
		int handleOffset = -1, pointerSize = Native.POINTER_SIZE;
		
		// scan for handle pointer offset
		for(int offset = pointerSize; offset < 0x800; offset += pointerSize) {
			long currentValue = safeReadLong(base, offset);
			if(currentValue == x11Window) {
				handleOffset = offset;
				break;
			}
		}
		
		if(handleOffset == -1) {
			return null;
		}
		
		long candidate = safeReadLong(base, handleOffset + pointerSize * 2);
		return candidate != 0 ? new Pointer(candidate) : null;
	}
	
	private long safeReadLong(Pointer pointer, int offset) {
		try {
			return pointer.getLong(offset);
		} catch (Throwable e) {
			return 0;
		}
	}
	
	@FieldOrder({"x", "y"})
	public static class XPoint extends Structure {
		public short x;
		public short y;
		
		public XPoint() {}
		public XPoint(int x, int y) {
			this.x = (short) x;
			this.y = (short) y;
		}
		
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("x", "y");
		}
	}
}
