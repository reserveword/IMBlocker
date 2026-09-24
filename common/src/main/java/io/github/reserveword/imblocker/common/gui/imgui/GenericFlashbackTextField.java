package io.github.reserveword.imblocker.common.gui.imgui;

import imgui.moulberry90.ImGui;
import imgui.moulberry90.ImGuiInputTextCallbackData;
import imgui.moulberry90.ImVec2;
import imgui.moulberry90.callback.ImGuiInputTextCallback;
import imgui.moulberry90.flag.ImGuiInputTextFlags;
import imgui.moulberry90.internal.ImGuiWindow;
import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.Rectangle;

public class GenericFlashbackTextField extends GenericImGuiTextField {

	private static final GenericFlashbackTextField INSTANCE = new GenericFlashbackTextField();
	
	private final FlashbackTextFieldCallback flashbackTextFieldCallback = new FlashbackTextFieldCallback();
	
	public static GenericFlashbackTextField getInstance() {
		return INSTANCE;
	}
	
	public ImGuiInputTextCallback getFlashbackTextFieldCallback(ImGuiInputTextCallback present) {
		flashbackTextFieldCallback.nested = present;
		return flashbackTextFieldCallback;
	}

	@Override
	protected int imgui$getID(String label) {
		return ImGui.getID(label);
	}
	
	@Override
	protected int imgui$getFontSize() {
		return ImGui.getFontSize();
	}
	
	@Override
	protected float imgui$getLabelWidth(String label) {
		return ImGui.calcTextSize(label, true).x;
	}
	
	@Override
	protected float imgui$getTextWidth(String text) {
		return ImGui.calcTextSize(text).x;
	}
	
	@Override
	protected float imgui$getInnerWidth() {
		return ImGui.getItemRectSizeX();
	}
	
	@Override
	protected Rectangle imgui$getBounds() {
		ImVec2 pos = ImGui.getItemRectMin();
		ImVec2 size = ImGui.getItemRectSize();
		return new Rectangle((int) pos.x, (int) pos.y, (int) size.x, Integer.MAX_VALUE);
	}
	
	@Override
	protected float imgui$getFramePaddingX() {
		return ImGui.getStyle().getFramePaddingX();
	}
	
	@Override
	protected float imgui$getFramePaddingY() {
		return ImGui.getStyle().getFramePaddingY();
	}
	
	@Override
	protected float imgui$getScrollX() {
		return ImGui.getScrollX();
	}
	
	@Override
	protected float imgui$getScrollY() {
		return ImGui.getScrollY();
	}
	
	@Override
	protected float imgui$getItemInnerSpacingX() {
		return ImGui.getStyle().getItemInnerSpacingX();
	}
	
	@Override
	protected float imgui$getScrollbarSize() {
		return ImGui.getStyle().getScrollbarSize();
	}
	
	@Override
	protected int imgui$getNoHorizontalScrollFlag() {
		return ImGuiInputTextFlags.NoHorizontalScroll;
	}
	
	protected boolean hasVerticalScrollBar() {
		try {
			ImGuiWindow currentWindow = imgui.moulberry90.internal.ImGui.getCurrentWindow();
			try {
				return currentWindow.getScrollbarY();
			} catch (NoSuchMethodError e) {
				return ReflectionUtil.invokeMethod(ImGuiWindow.class, currentWindow, 
						boolean.class, "isScrollbarY", new Class[0]);
			}
		} catch (Throwable e) {
			try {
				return ImGui.getScrollMaxY() > 0;
			} catch (Throwable e2) {
				return false; //Absolute Anti-Crash.
			}
		}
	}
	
	private class FlashbackTextFieldCallback extends ImGuiInputTextCallback {
		
		private ImGuiInputTextCallback nested;
		
		@Override
		public void accept(ImGuiInputTextCallbackData t) {
			if(nested != null) {
				nested.accept(t);
			}
			
			if((t.getEventFlag() & ImGuiInputTextFlags.CallbackResize) == 0) {
				if(!isTrulyFocused()) {
					FocusContainer.IMGUI.requestFocus(GenericFlashbackTextField.this);
				}
				updateTextFieldGUIProperties(t.getBuf(), t.getCursorPos());
			}
		}
	}
}
