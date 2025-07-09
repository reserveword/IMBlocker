package io.github.reserveword.imblocker.common.gui;

import java.util.Arrays;

import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.ImVec2;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.internal.ImGuiWindow;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.StringUtil;

public class GenericAxiomTextField implements FocusableWidget {
	
	private static final GenericAxiomTextField INSTANCE = new GenericAxiomTextField();
	
	private static final AxiomTextFieldCallback axiomTextFieldCallback = new AxiomTextFieldCallback();
	
	private static boolean isMultiline = false;
	private static int currentItemId = 0;
	private static int activeItemId = 0;
	private static String itemLabel = "";

	private static int fontHeight = 8;
	private static Rectangle bounds = Rectangle.EMPTY;
	private static Point caretPos = Point.TOP_LEFT;
	
	private static int inputTextFlags = 0;
	private static float internalScrollX = 0.0f;
	
	private static String text = "";
	private static int[] lineData = {0};
	private static int[] beginIndexData = {0};

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
	
	public static void setMultiline(boolean multiline) {
		isMultiline = multiline;
	}
	
	public static void setLabel(String label) {
		currentItemId = ImGui.getID(label);
		itemLabel = label;
	}
	
	public static void setInputTextFlags(int flags) {
		inputTextFlags = flags;
	}
	
	private static void updateTextFieldGUIProperties(ImGuiInputTextCallbackData axiomTextFieldData) {
		int currentFontHeight;
		Rectangle currentBounds;
		Point currentCaretPos;

		currentFontHeight = ImGui.getFontSize();
		ImVec2 pos = ImGui.getItemRectMin();
		ImVec2 size = ImGui.getItemRectSize();
		currentBounds = new Rectangle((int) pos.x, (int) pos.y, (int) size.x, Integer.MAX_VALUE);
		if(axiomTextFieldData != null) {
			String currentText = axiomTextFieldData.getBuf();
			if(!text.equals(currentText)) {
				text = currentText;
				splitLines(text);
			}
			int cursorPos =  new String(Arrays.copyOfRange(
					currentText.getBytes(), 0, axiomTextFieldData.getCursorPos())).length();
			float cursorX = ImGui.calcTextSize(StringUtil
					.getSubstring(text, beginIndexData[cursorPos], cursorPos)).x;
			updateInternalScrollX(cursorX);
			int caretX = (int) (ImGui.getStyle().getFramePaddingX() + 
					cursorX - internalScrollX - ImGui.getScrollX());
			int caretY = (int) (ImGui.getStyle().getFramePaddingY() + 
					lineData[cursorPos] * currentFontHeight - ImGui.getScrollY());
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
		if(activeItemId != currentItemId) {
			activeItemId = currentItemId;
			internalScrollX = 0;
		}
		
		float innerWidth = ImGui.getItemRectSizeX();
		if(!isMultiline) {
			if(!itemLabel.startsWith("##")) {
				float labelWidth = ImGui.calcTextSize(itemLabel, true).x;
				if (labelWidth > 0) { //imgui_widgets.cpp#L3889
					innerWidth -= (labelWidth + ImGui.getStyle().getItemInnerSpacingX());
				} 
			}
		}else {
			if(hasVerticalScrollBar()) {
				innerWidth -= ImGui.getStyle().getScrollbarSize(); //imgui_wigets.cpp#L3922
			}
		}
		if((inputTextFlags & ImGuiInputTextFlags.NoHorizontalScroll) == 0) { //imgui_wigets.cpp#L4528
			final float scrollIncrementX = innerWidth * 0.25f;
			final float visibleWidth = innerWidth - ImGui.getStyle().getFramePaddingX();
			if(cursorOffsetX < internalScrollX) {
				internalScrollX = (float) Math.floor(Math.max(0.0f, cursorOffsetX - scrollIncrementX));
			}else if((cursorOffsetX - visibleWidth) >= internalScrollX) {
				internalScrollX = (float) Math.floor(cursorOffsetX - visibleWidth + scrollIncrementX);
			}
			if(System.currentTimeMillis() % 100 < 1) System.out.println(internalScrollX);
		}else {
			internalScrollX = 0.0f;
		}
	}
	
	private static boolean hasVerticalScrollBar() {
		try {
			ImGuiWindow currentWindow = imgui.internal.ImGui.getCurrentWindow();
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
	
	private static void splitLines(String text) {
		lineData = new int[text.length() + 1];
		beginIndexData = new int[text.length() + 1];
		int charIndex, currentLine = 0, currentLineBeginIndex = 0;
		for(charIndex = 0; charIndex < text.length(); charIndex++) {
			lineData[charIndex] = currentLine;
			beginIndexData[charIndex] = currentLineBeginIndex;
			char currentChar = text.charAt(charIndex);
			if(currentChar == '\n') {
				currentLine++;
				currentLineBeginIndex = charIndex + 1;
			}
		}
		lineData[charIndex] = currentLine;
		beginIndexData[charIndex] = currentLineBeginIndex;
	}
	
	private static class AxiomTextFieldCallback extends ImGuiInputTextCallback {
		
		private static ImGuiInputTextCallback nested;
		
		@Override
		public void accept(ImGuiInputTextCallbackData t) {
			//Pass to existed callback.
			if(nested != null) {
				nested.accept(t);
			}
			
			//Filter resize callback since it provides invalid data.
			if((t.getEventFlag() & ImGuiInputTextFlags.CallbackResize) == 0) {
				if(!INSTANCE.isTrulyFocused()) {
					FocusContainer.IMGUI.requestFocus(INSTANCE);
				}
				updateTextFieldGUIProperties(t);
			}
		}
	}
}
