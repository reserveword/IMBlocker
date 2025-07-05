package io.github.reserveword.imblocker.common.gui;

import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.ImVec2;
import imgui.callback.ImGuiInputTextCallback;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.StringUtil;

public class GenericAxiomTextField implements FocusableWidget {
	
	private static final GenericAxiomTextField INSTANCE = new GenericAxiomTextField();
	
	private static final AxiomTextFieldCallback axiomTextFieldCallback = new AxiomTextFieldCallback();

	private static int fontHeight = 8;
	private static Rectangle bounds = Rectangle.EMPTY;
	private static Point caretPos = Point.TOP_LEFT;

	@Override
	public boolean getPreferredState() {
		return true;
	}

	@Override
	public Rectangle getBoundsAbs() {
		return bounds;
	}

	@Override
	public Point getCaretPos() {
		return caretPos;
	}
	
	@Override
	public int getFontHeight() {
		return fontHeight;
	}

	@Override
	public FocusContainer getFocusContainer() {
		return FocusContainer.IMGUI;
	}
	
	public static GenericAxiomTextField getInstance() {
		return INSTANCE;
	}
	
	public static ImGuiInputTextCallback getAxiomTextFieldCallback(ImGuiInputTextCallback present) {
		AxiomTextFieldCallback.nested = present;
		return axiomTextFieldCallback;
	}
	
	private static void updateTextFieldGUIProperties(ImGuiInputTextCallbackData axiomTextFieldData) {
		Rectangle currentBounds;
		Point currentCaretPos;
		int currentFontHeight;
		
		ImVec2 startPos = ImGui.getItemRectMin();
		ImVec2 endPos = ImGui.getItemRectMax();
		int x = (int) startPos.x;
		int y = (int) startPos.y;
		int width = (int) (endPos.x - startPos.x);
		int height = (int) (endPos.y - startPos.y);
		currentBounds = new Rectangle(x, y, width, height);
		if(axiomTextFieldData != null) {
			String text = axiomTextFieldData.getBuf();
			int cursorPos = axiomTextFieldData.getCursorPos();
			int caretX = (int) ImGui.calcTextSize(StringUtil.getSubstring(text, 0, cursorPos)).x;
			currentCaretPos = new Point(caretX, 0);
		}else {
			currentCaretPos = Point.TOP_LEFT;
		}
		currentFontHeight = ImGui.getFontSize();
		
		if(!bounds.equals(currentBounds) || !caretPos.equals(currentCaretPos)) {
			bounds = currentBounds;
			caretPos = currentCaretPos;
			IMManager.updateCompositionWindowPos();
		}
		
		if(fontHeight != currentFontHeight) {
			fontHeight = currentFontHeight;
			IMManager.updateCompositionFontSize();
		}
	}
	
	private static class AxiomTextFieldCallback extends ImGuiInputTextCallback {
		
		private static ImGuiInputTextCallback nested;
		
		@Override
		public void accept(ImGuiInputTextCallbackData t) {
			if(nested != null) {
				nested.accept(t);
			}
			updateTextFieldGUIProperties(t);
		}
	}
}
