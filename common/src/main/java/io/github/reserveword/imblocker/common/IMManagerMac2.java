package io.github.reserveword.imblocker.common;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.glfw.GLFWNativeCocoa;

import com.sun.jna.Callback;
import com.sun.jna.CallbackReference;
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
import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;

final class IMManagerMac2 implements IMManager.PlatformIMManager {
	private static final boolean IS_X86 = "x86_64".equalsIgnoreCase(System.getProperty("os.arch")) || 
			"amd64".equalsIgnoreCase(System.getProperty("os.arch"));
	
	private static boolean state = false;
	static private final Pointer viewClass = Runtime.INSTANCE.objc_getClass("GLFWContentView");
	static private Pointer view = null;
	
	private static final KeyDownCallback KeyDownImp;
	private static final KeyDownCallback NewKeyDownImp;
	
	static private final InterpretKeyEventsCallback InterpretKeyImp;
	static private final InterpretKeyEventsCallback NewInterpretKeyImp;
	
	private static final InsertTextCallback InsertTextImp;
	private static final InsertTextCallback NewInsertTextImp;
	
	private static final FirstRectForCharacterRangeCallback NewFirstRectImp;
	private static double preeditX, preeditY, preeditW, preeditH;
	
	private static final SetMarkedTextCallback SetMarkedTextImp;
	private static final SetMarkedTextCallback NewSetMarkedTextImp;
	private static boolean markedTextProcessed = false;

	static {
		// keyDown replacement \\
		Pointer keyDownSelector = RuntimeUtils.sel("keyDown:");
		Pointer interpretKeySelector = RuntimeUtils.sel("interpretKeyEvents:");
		Pointer keyDownMethod = Runtime.INSTANCE.class_getInstanceMethod(viewClass, keyDownSelector);
		KeyDownImp = getImp(KeyDownCallback.class, keyDownMethod);
		NewKeyDownImp = (self, _cmd, event) -> {
			if (RuntimeUtils.msg(self, RuntimeUtils.sel("hasMarkedText")) == 0) {
				KeyDownImp.invoke(self, _cmd, event);
			} else {
				markedTextProcessed = false;
				long events = RuntimeUtils.msg(RuntimeUtils.cls("NSArray"), RuntimeUtils.sel("arrayWithObject:"), event);
				RuntimeUtils.msg(self, interpretKeySelector, events);
				if (!markedTextProcessed) {
					RuntimeUtils.msg(self, RuntimeUtils.sel("unmarkText"));
					postPreeditContent(null, 0);
				}
			}
		};
		ObjC.INSTANCE.class_replaceMethod(viewClass, keyDownSelector, NewKeyDownImp, "v@:@");
		
		// interpretKey replacement \\
		Pointer interpretKeyMethod = Runtime.INSTANCE.class_getInstanceMethod(viewClass, interpretKeySelector);
		InterpretKeyImp = getImp(InterpretKeyEventsCallback.class, interpretKeyMethod);
		final NSRange emptyRange = new NSRange();
		emptyRange.location = Long.MAX_VALUE;
		emptyRange.length = 0;
		NewInterpretKeyImp = (self, sel, eventArray) -> {
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
			InterpretKeyImp.invoke(self, sel, eventArray);
		};
		ObjC.INSTANCE.class_replaceMethod(viewClass, interpretKeySelector, NewInterpretKeyImp, "v@:@");
		
		// insertText replacement \\
		Pointer insertTextSelector = RuntimeUtils.sel("insertText:replacementRange:");
		Pointer insertTextMethod = Runtime.INSTANCE.class_getInstanceMethod(viewClass, insertTextSelector);
		InsertTextImp = getImp(InsertTextCallback.class, insertTextMethod);
		NewInsertTextImp = (self, selector, string, location, length) -> {
			InsertTextImp.invoke(self, selector, string, location, length);
			RuntimeUtils.msg(self, RuntimeUtils.sel("unmarkText"));
			postPreeditContent(null, 0);
		};
		ObjC.INSTANCE.class_replaceMethod(viewClass, insertTextSelector, NewInsertTextImp, "v@:@{_NSRange=QQ}");
		
		// firstRectForCharacterRange replacement \\
		Pointer firstRectSel = RuntimeUtils.sel("firstRectForCharacterRange:actualRange:");
		NewFirstRectImp = (self, sel, rangePtr, actualRange) -> {
			Pointer window = new Pointer(GLFWNativeCocoa.glfwGetCocoaWindow(
					MinecraftClientAccessor.INSTANCE.getWindowHandle()));
			NSRect contentRect = getContentRect(window, getWindowFrame(window));
			double caretScreenX = contentRect.x + preeditX;
			double caretScreenY = contentRect.y + contentRect.height - preeditY - preeditH;
			NSRect cursorRect = new NSRect(caretScreenX, caretScreenY, preeditW, preeditH);
			cursorRect.write();
			return cursorRect;
		};
		ObjC.INSTANCE.class_replaceMethod(viewClass, firstRectSel, NewFirstRectImp,
				"{CGRect={CGPoint=dd}{CGSize=dd}}@:{_NSRange=QQ}^{_NSRange=QQ}");
		
		// setMarkedText replacement \\
		Pointer setMarkedTextSelector = RuntimeUtils.sel("setMarkedText:selectedRange:replacementRange:");
		Pointer setMarkedTextMethod = Runtime.INSTANCE.class_getInstanceMethod(viewClass, setMarkedTextSelector);
		SetMarkedTextImp = getImp(SetMarkedTextCallback.class, setMarkedTextMethod);
		NewSetMarkedTextImp = (self, sel, stringPtr, selectedRange, replacementRange) -> {
			SetMarkedTextImp.invoke(self, setMarkedTextSelector, stringPtr, selectedRange, replacementRange);
			markedTextProcessed = true;
			
			if (stringPtr == null) {
				postPreeditContent(null, 0);
				return;
			}
			
			Proxy string = Proxy.load(stringPtr);
			boolean isAttributed = string.sendBoolean("isKindOfClass:", RuntimeUtils.cls("NSAttributedString"));
			String text = null; 
			try {
				text = isAttributed ? string.sendString("string") : string.sendString("description");
			} catch (Throwable e) {}
			
			if (text == null || text.isEmpty()) {
				postPreeditContent(null, 0);
				return;
			}
			
			int selectedLocation = clampUtf16Index(selectedRange.location, text.length());
			if (!isAttributed) {
				StringBuilder result = new StringBuilder(text.length());
				appendVisibleText(result, text, 0, text.length());
				postPreeditContent(result.toString(), selectedLocation);
			} else {
				StringBuilder result = new StringBuilder(text.length() + 8);
				int searchLocation = 0;
				NSRange.ByReference effectiveRange = new NSRange.ByReference();
				try {
					while (searchLocation < text.length()) {
						effectiveRange.location = 0;
						effectiveRange.length = 0;
						string.send("attributesAtIndex:effectiveRange:", searchLocation, effectiveRange);
						effectiveRange.read();
						
						int blockLocation = effectiveRange.getLocation(), blockLength = effectiveRange.getLength();
						if (blockLength <= 0 || blockLocation > text.length()) break;
						long end = blockLocation + blockLength;
						if (end <= searchLocation) break;
						int blockEnd = (int) Math.min(end, text.length());
						if (result.length() > 0) result.append(' ');
						appendVisibleText(result, text, blockLocation, blockEnd);
						searchLocation = blockEnd;
					}
					postPreeditContent(result.toString(), selectedLocation);
				} finally {
					effectiveRange.clear();
				}
			}
		};
		ObjC.INSTANCE.class_replaceMethod(viewClass, setMarkedTextSelector, NewSetMarkedTextImp, 
				ObjC.INSTANCE.method_getTypeEncoding(setMarkedTextMethod));
	}

	/**
	 * @see <a href=
	 *      "https://developer.apple.com/documentation/objectivec/objective-c_runtime">Apple
	 *      Developer Documentation for objc_runtime:</a>
	 */
	private interface ObjC extends Library {
		ObjC INSTANCE = Native.load("objc.A", ObjC.class);

		void class_replaceMethod(Pointer cls, Pointer selector, Callback imp, String types);

		Pointer method_getImplementation(Pointer selector);
		
		String method_getTypeEncoding(Pointer selector);
		
		NSRect objc_msgSend(Pointer receiver, Pointer selector);
		NSRect objc_msgSend(Pointer receiver, Pointer selector, NSRect frame);
		void objc_msgSend_stret(Pointer result, Pointer receiver, Pointer selector);
		void objc_msgSend_stret(Pointer result, Pointer receiver, Pointer slector, NSRect frame);
	}
	
	private interface KeyDownCallback extends Callback {
		void invoke(Pointer self, Pointer _cmd, Pointer event);
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
	
	private interface InsertTextCallback extends Callback {
		void invoke(Pointer self, Pointer selector, Pointer string, long location, long length);
	}
	
	private interface FirstRectForCharacterRangeCallback extends Callback {
		NSRect invoke(Pointer self, Pointer selector, NSRange.ByValue range, Pointer actualRange);
	}
	
	private interface SetMarkedTextCallback extends Callback {
		void invoke(Pointer self, Pointer selector, Pointer string, NSRange.ByValue selectedRange, NSRange.ByValue replacementRange);
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
	public void setPreeditCursorRectangle(int x, int y, int width, int height) {
		preeditX = x;
		preeditY = y;
		preeditW = width;
		preeditH = height;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Callback> T getImp(Class<T> callbackClass, Pointer selector) {
		return (T) CallbackReference.getCallback(callbackClass, ObjC.INSTANCE.method_getImplementation(selector));
	}
	
	private static NSRect getWindowFrame(Pointer window) {
		Pointer sel = RuntimeUtils.sel("frame");
		
		if (!IS_X86) {
			return ObjC.INSTANCE.objc_msgSend(window, sel);
		}
		
		NSRect result = new NSRect();
		ObjC.INSTANCE.objc_msgSend_stret(result.getPointer(), window, sel);
		result.read();
		return result;
	}
	
	private static NSRect getContentRect(Pointer window, NSRect frame) {
		Pointer sel = RuntimeUtils.sel("contentRectForFrameRect:");
		frame.write();
		
		if (!IS_X86) {
			return ObjC.INSTANCE.objc_msgSend(window, sel, frame);
		}
		
		NSRect result = new NSRect();
		ObjC.INSTANCE.objc_msgSend_stret(result.getPointer(), window, sel, frame);
		result.read();
		return result;
	}
	
	private static void postPreeditContent(String compositionString, int caretPosition) {
		IMBlockerCore.invokeOnRenderThread(() -> UniversalIMEPreeditOverlay.getInstance()
				.preeditContentUpdated(compositionString, caretPosition));
	}
	
	private static void appendVisibleText(StringBuilder out, String text, int start, int end) {
		int codePoint, charCount;
		for (int i = start; i < end; i += charCount) {
			codePoint = text.codePointAt(i);
			charCount = Character.charCount(codePoint);
			if (codePoint < 0xF700 || codePoint > 0xF7FF) {
				out.appendCodePoint(codePoint);
			}
		}
	}

	private static int clampUtf16Index(long value, int textLength) {
		return value <= 0 ? 0 : (value >= textLength ? textLength : (int) value);
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
