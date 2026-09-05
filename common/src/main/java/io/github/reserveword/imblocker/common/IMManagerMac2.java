package io.github.reserveword.imblocker.common;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.glfw.GLFWNativeCocoa;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

import ca.weblite.objc.Proxy;
import ca.weblite.objc.Runtime;
import ca.weblite.objc.RuntimeUtils;
import ca.weblite.objc.foundation.NSRange;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.Point;

final class IMManagerMac2 implements IMManager.PlatformIMManager {
	private static boolean state = false;
	static private final Pointer viewClass = Runtime.INSTANCE.objc_getClass("GLFWContentView");
	static private Pointer view = null;
	static private final InterpretKeyEventsCallback Imp;
	static private final InterpretKeyEventsCallback NewImp;
	
	private static final FirstRectForCharacterRangeCallback NewFirstRectImp;
	private static int fontSize;
	private static double caretX, caretY;

	static {
		// see
		// https://github.com/glfw/glfw/blob/b4c3ef9d0fdf46845f3e81e5d989dab06e71e6c1/src/cocoa_window.m#L571
		// Replacing the method dynamically to determine whether to send text based on
		// state
		// see reference for objc_runtime's dynamic manipulation at
		// https://developer.apple.com/documentation/objectivec/objective-c_runtime
		Pointer selector = RuntimeUtils.sel("interpretKeyEvents:");
		Pointer method = Runtime.INSTANCE.class_getInstanceMethod(viewClass, selector);
		Imp = ObjC.INSTANCE.method_getImplementation(method);
		final NSRange emptyRange = new NSRange();
		emptyRange.location = Long.MAX_VALUE;
		emptyRange.length = 0;
		NewImp = (self, sel, eventArray) -> {
			if (view == null) view = self;
			if (!state) {
				Pointer textInputContextCls = RuntimeUtils.cls("NSTextInputContext");
				Pointer currentCtx = RuntimeUtils.msgPointer(textInputContextCls, "currentInputContext");
				if (Pointer.nativeValue(currentCtx) != 0) {
					RuntimeUtils.msg(currentCtx, "discardMarkedText");
				}
				if (Pointer.nativeValue(eventArray) != 0) {
					long count = RuntimeUtils.msg(eventArray, "count");
					for (long i = 0; i < count; i++) {
						Pointer event = RuntimeUtils.msgPointer(eventArray, "objectAtIndex:", i);
						Pointer characters = RuntimeUtils.msgPointer(event, "characters");
						if (Pointer.nativeValue(characters) != 0) {
							RuntimeUtils.msg(self, "insertText:replacementRange:", characters, emptyRange);
						}
					}
				}
				return;
			}
			Imp.invoke(self, sel, eventArray);
		};
		ObjC.INSTANCE.class_replaceMethod(viewClass, selector, NewImp, "v@:@");
		
		Pointer firstRectSel = RuntimeUtils.sel("firstRectForCharacterRange:actualRange:");
		NewFirstRectImp = (self, sel, rangePtr, actualRange) -> {
			Proxy window = new Proxy(new Pointer(GLFWNativeCocoa.glfwGetCocoaWindow(
					MinecraftClientAccessor.INSTANCE.getWindowHandle())));
			NSRect contentRect = new NSRect(0, 0, 0, 0); //toNSRect(window.send("contentRectForFrameRect:", toNSRect(window.send("frame"))));
			double caretScreenX = contentRect.x + caretX;
			double caretScreenY = contentRect.y + contentRect.height - caretY - fontSize;
			return new NSRect(caretScreenX, caretScreenY, 0, fontSize);
		};
		ObjC.INSTANCE.class_replaceMethod(viewClass, firstRectSel, NewFirstRectImp,
				"{CGRect={CGPoint=dd}{CGSize=dd}}@:{_NSRange=QQ}^{_NSRange=QQ}");
	}

	/**
	 * @see <a href=
	 *      "https://developer.apple.com/documentation/objectivec/objective-c_runtime">Apple
	 *      Developer Documentation for objc_runtime:</a>
	 */
	private interface ObjC extends Library {
		ObjC INSTANCE = Native.load("objc.A", ObjC.class);

		void class_replaceMethod(Pointer cls, Pointer selector, Callback imp, String types);

		InterpretKeyEventsCallback method_getImplementation(Pointer selector);
	}

	/**
	 * The underlying native type is IMP, which should be a function pointer to the
	 * implementation of interpretKeyEvents:
	 * 
	 * @see <a href=
	 *      "https://developer.apple.com/documentation/objectivec/objective-c_runtime/imp">Documentation
	 *      for IMP</a>
	 * @see <a href=
	 *      "https://developer.apple.com/documentation/appkit/nsresponder/1531599-interpretkeyevents?language=objc">Documentation
	 *      for interpretKeyEvents:</a>
	 */
	private interface InterpretKeyEventsCallback extends Callback {
		/**
		 * @param self       "this" pointer for NSObject
		 * @param selector   selector for interpretKeyEvents:
		 * @param eventArray an array of NSEvent objects
		 */
		void invoke(Pointer self, Pointer selector, Pointer eventArray);
	}
	
	private interface FirstRectForCharacterRangeCallback extends Callback {
		NSRect invoke(Pointer self, Pointer selector, NSRange.ByValue range, Pointer actualRange);
	}

	@Override
	public void setState(boolean on) {
		if (state != on) {
			state = on;
		}
	}

	@Override
	public void setEnglishState(boolean isEN) {

	}
	
	@Override
	public void updateCompositionWindowPos(Point pos) {
		caretX = pos.x();
		caretY = pos.y();
	}
	
	@Override
	public void updateCompositionFontSize(int newFontSize) {
		fontSize = newFontSize;
	}
	
	private static NSRect toNSRect(Object obj) {
		if (obj instanceof NSRect) {
			return (NSRect) obj;
		} else if (obj instanceof Structure) {
			NSRect rect = new NSRect(((Structure) obj).getPointer());
			return rect;
		} else if (obj instanceof Pointer) {
			return new NSRect((Pointer) obj);
		} else {
			return new NSRect();
		}
	}
	
	@FieldOrder({"x", "y", "width", "height"})
	public static class NSRect extends Structure implements Structure.ByValue {
		public double x;
		public double y;
		public double width;
		public double height;

		public NSRect() {}
		
		public NSRect(Pointer peer) {
			super(peer);
			read();
		}

		public NSRect(double x, double y, double width, double height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("x", "y", "width", "height");
		}
	}
}
