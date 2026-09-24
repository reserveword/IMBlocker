package io.github.reserveword.imblocker.common.gui.imgui;

import imgui.moulberry92.ImGui;
import imgui.moulberry92.ImGuiInputTextCallbackData;
import imgui.moulberry92.ImVec2;
import imgui.moulberry92.callback.ImGuiInputTextCallback;
import imgui.moulberry92.flag.ImGuiInputTextFlags;
import imgui.moulberry92.internal.ImGuiWindow;
import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.Rectangle;

public final class GenericAxiomTextField extends GenericImGuiTextField {
	
	private static final GenericAxiomTextField INSTANCE = new GenericAxiomTextField();
	
	private final AxiomTextFieldCallback axiomTextFieldCallback = new AxiomTextFieldCallback();
	
	public static GenericAxiomTextField getInstance() {
		return INSTANCE;
	}
	
	public ImGuiInputTextCallback getAxiomTextFieldCallback(ImGuiInputTextCallback present) {
		axiomTextFieldCallback.nested = present;
		return axiomTextFieldCallback;
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
			ImGuiWindow currentWindow = imgui.moulberry92.internal.ImGui.getCurrentWindow();
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
	
	private class AxiomTextFieldCallback extends ImGuiInputTextCallback {
		
		private ImGuiInputTextCallback nested;
		
		@Override
		public void accept(ImGuiInputTextCallbackData t) {
			//Pass to existed callback.
			if(nested != null) {
				nested.accept(t);
			}
			
			//Filter resize callback since it provides invalid data.
			if((t.getEventFlag() & ImGuiInputTextFlags.CallbackResize) == 0) {
				if(!isTrulyFocused()) {
					FocusContainer.IMGUI.requestFocus(GenericAxiomTextField.this);
				}
				updateTextFieldGUIProperties(t.getBuf(), t.getCursorPos()); //Overhead: ~7000ns
			}
		}
	}
}
