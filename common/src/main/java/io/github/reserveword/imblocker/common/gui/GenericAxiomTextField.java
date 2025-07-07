package io.github.reserveword.imblocker.common.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.ImVec2;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import io.github.reserveword.imblocker.common.IMManager;

public class GenericAxiomTextField implements FocusableWidget {
	
	private static final GenericAxiomTextField INSTANCE = new GenericAxiomTextField();
	
	private static final AxiomTextFieldCallback axiomTextFieldCallback = new AxiomTextFieldCallback();
	
	private static final int ImGuiInputTextFlags_Multiline = 1 << 26;
	
	private static String currentItemLabel = "";
	private static String itemLabel = "";

	private static int fontHeight = 8;
	private static Rectangle bounds = Rectangle.EMPTY;
	private static Point caretPos = Point.TOP_LEFT;
	
	private static int inputTextFlags = 0;
	private static float internalScrollX = 0.0f;
	
	private static String textBeforeCursor = "";
	private static int lineCountBeforeCursor = 0;
	private static String textLineBeforeCursor = "";

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
	
	public static void setLabel(String newLabel) {
		currentItemLabel = newLabel;
	}
	
	public static void setInputTextFlags(int flags) {
		inputTextFlags = flags;
	}
	
	private static void updateTextFieldGUIProperties(ImGuiInputTextCallbackData axiomTextFieldData) {
		int currentFontHeight;
		Rectangle currentBounds;
		Point currentCaretPos;
		String currentTextBeforeCursor;

		currentFontHeight = ImGui.getFontSize();
		ImVec2 pos = ImGui.getItemRectMin();
		ImVec2 size = ImGui.getItemRectSize();
		currentBounds = new Rectangle((int) pos.x, (int) pos.y, (int) size.x, Integer.MAX_VALUE);
		if(axiomTextFieldData != null) {
			int cursorPos = axiomTextFieldData.getCursorPos();
			currentTextBeforeCursor = new String(Arrays.copyOfRange(
					axiomTextFieldData.getBuf().getBytes(), 0, cursorPos));
			if(!textBeforeCursor.equals(currentTextBeforeCursor)) {
				textBeforeCursor = currentTextBeforeCursor;
				String[] lines = splitLines(textBeforeCursor);
				lineCountBeforeCursor = lines.length - 1;
				textLineBeforeCursor = lines[lineCountBeforeCursor];
			}
			float cursorX = ImGui.calcTextSize(textLineBeforeCursor).x;
			updateInternalScrollX(cursorX);
			int caretX = (int) (ImGui.getStyle().getFramePaddingX() + 
					cursorX - internalScrollX - ImGui.getScrollX());
			int caretY = (int) (ImGui.getStyle().getFramePaddingY() + 
					lineCountBeforeCursor * currentFontHeight - ImGui.getScrollY());
			currentCaretPos = new Point(caretX, caretY);
		}else {
			currentCaretPos = Point.TOP_LEFT;
		}
		
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
	
	/**
	 * Manually maintain the internal horizontal scroll amount of the text field
	 * because of the lack of corresponding APIs. Calculations are taken from
	 * {@code imgui_widgets.cpp}.
	 */
	private static void updateInternalScrollX(float cursorOffsetX) {
		if(!itemLabel.equals(currentItemLabel)) {
			itemLabel = currentItemLabel;
			internalScrollX = 0;
		}
		
		float innerWidth = ImGui.calcItemWidth();
		if((inputTextFlags & ImGuiInputTextFlags_Multiline) != 0/*isMultiline*/) {
			innerWidth -= ImGui.getStyle().getScrollbarSize(); //imgui_wigets.cpp#L3922
		}
		if((inputTextFlags & ImGuiInputTextFlags.NoHorizontalScroll) == 0) { //imgui_wigets.cpp#L4528
			final float scrollIncrementX = innerWidth * 0.25f;
			final float visibleWidth = innerWidth - ImGui.getStyle().getFramePaddingX();
			if(cursorOffsetX < internalScrollX) {
				internalScrollX = (float) Math.floor(Math.max(0.0f, cursorOffsetX - scrollIncrementX));
			}else if((cursorOffsetX - visibleWidth) >= internalScrollX) {
				internalScrollX = (float) Math.floor(cursorOffsetX - visibleWidth + scrollIncrementX);
			}
		}else {
			internalScrollX = 0.0f;
		}
	}
	
	private static String[] splitLines(String text) {
		List<String> lines = new ArrayList<>();
		String currentLine = "";
		for(int i = 0; i < text.length(); i++) {
			char currentChar = text.charAt(i);
			currentLine += currentChar;
			if(currentChar == '\n') {
				lines.add(currentLine);
				currentLine = "";
			}
		}
		lines.add(currentLine);
		return lines.toArray(new String[lines.size()]);
	}
	
	private static class AxiomTextFieldCallback extends ImGuiInputTextCallback {
		
		private static ImGuiInputTextCallback nested;
		
		@Override
		public void accept(ImGuiInputTextCallbackData t) {
			if(nested != null) {
				nested.accept(t);
			}
			if((t.getEventFlag() & ImGuiInputTextFlags.CallbackResize) == 0) {
				updateTextFieldGUIProperties(t);
			}
		}
	}
}
