package io.github.reserveword.imblocker.common.gui;

import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.ImVec2;
import imgui.callback.ImGuiInputTextCallback;

public class GenericAxiomTextField implements FocusableWidget {
	
	private static final AxiomTextFieldCallback axiomTextFieldCallback = new AxiomTextFieldCallback();
	private static ImGuiInputTextCallbackData axiomTextFieldData;

	@Override
	public boolean getPreferredState() {
		return true;
	}

	@Override
	public Rectangle getBoundsAbs() {
		ImVec2 startPos = ImGui.getItemRectMin();
		ImVec2 endPos = ImGui.getItemRectMax();
		int x = (int) startPos.x;
		int y = (int) startPos.y;
		int width = (int) (endPos.x - startPos.x);
		int height = (int) (endPos.y - startPos.y);
		return new Rectangle(x, y, width, height);
	}

	@Override
	public Point getCaretPos() {
		if(axiomTextFieldData != null) {
			int cursorPos = axiomTextFieldData.getCursorPos();
			
		}
		return Point.TOP_LEFT;
	}
	
	@Override
	public int getFontHeight() {
		return (int) ImGui.getTextLineHeight();
	}

	@Override
	public FocusContainer getFocusContainer() {
		return FocusContainer.IMGUI;
	}
	
	public static ImGuiInputTextCallback getAxiomTextFieldCallback(ImGuiInputTextCallback present) {
		AxiomTextFieldCallback.nested = present;
		return axiomTextFieldCallback;
	}
	
	private static class AxiomTextFieldCallback extends ImGuiInputTextCallback {
		
		private static ImGuiInputTextCallback nested;
		
		@Override
		public void accept(ImGuiInputTextCallbackData t) {
			if(nested != null) {
				nested.accept(t);
			}
			axiomTextFieldData = t;
		}
	}
}
