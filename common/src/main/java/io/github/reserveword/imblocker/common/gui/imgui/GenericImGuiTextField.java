package io.github.reserveword.imblocker.common.gui.imgui;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusableWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;

public abstract class GenericImGuiTextField implements FocusableWidget {
	
	private boolean isMultiline = false;
	private int currentItemId = 0;
	private int activeItemId = 0;
	private String itemLabel = "";

	private int fontHeight = 8;
	private Rectangle bounds = Rectangle.EMPTY;
	private Point caretPos = Point.TOP_LEFT;
	
	private int inputTextFlags = 0;
	private float internalScrollX = 0.0f;
	
	private String text = "";
	private String textBeforeCursor = "";
	private float cursorX = 0;
	private int[] lineData = {0};
	private int[] beginIndexData = {0};

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
	
	public void setMultiline(boolean multiline) {
		isMultiline = multiline;
	}
	
	public void setLabel(String label) {
		currentItemId = imgui$getID(label);
		itemLabel = label;
	}
	
	public void setInputTextFlags(int flags) {
		inputTextFlags = flags;
	}
	
	protected void updateTextFieldGUIProperties(String currentText, int rawCursorPos) {
		int currentFontHeight = imgui$getFontSize();
		Rectangle currentBounds = imgui$getBounds();
		Point currentCaretPos;
		
		if(!text.equals(currentText)) {
			text = currentText;
			splitLines(text);
		}
		int cursorPos = new String(Arrays.copyOfRange(currentText.getBytes(StandardCharsets.UTF_8), 
				0, rawCursorPos), StandardCharsets.UTF_8).length();
		String currentTextBeforeCursor = StringUtil
				.getSubstring(text, beginIndexData[cursorPos], cursorPos);
		if(!textBeforeCursor.equals(currentTextBeforeCursor)) {
			textBeforeCursor = currentTextBeforeCursor;
			cursorX = imgui$getTextWidth(textBeforeCursor);
			updateInternalScrollX(cursorX);
		}
		int caretX = (int) (imgui$getFramePaddingX() + 
				cursorX - internalScrollX - imgui$getScrollX());
		int caretY = (int) (imgui$getFramePaddingY() + 
				lineData[cursorPos] * currentFontHeight - imgui$getScrollY());
		currentCaretPos = new Point(caretX, caretY);
		
		if(!bounds.equals(currentBounds) || !caretPos.equals(currentCaretPos)) {
			bounds = currentBounds;
			caretPos = currentCaretPos;
			IMManager.updateCaretPosition();
		}
		
		if(fontHeight != currentFontHeight) {
			fontHeight = currentFontHeight;
			IMManager.updateCaretPosition();
			IMManager.updateCompositionFontSize();
		}
	}
	
	/**
	 * <p>Manually maintain the internal horizontal scroll amount of the text field
	 * because of the lack of corresponding APIs. Calculations are taken from
	 * {@code imgui_widgets.cpp}.
	 * 
	 * <p><i>This workaround may not be 100% accurate.</i>
	 */
	private void updateInternalScrollX(float cursorOffsetX) {
		if(activeItemId != currentItemId) {
			activeItemId = currentItemId;
			internalScrollX = 0;
		}
		
		float innerWidth = imgui$getInnerWidth();
		if(!isMultiline) {
			if(!itemLabel.startsWith("##")) {
				float labelWidth = imgui$getLabelWidth(itemLabel);
				if (labelWidth > 0) { //imgui_widgets.cpp#L3889
					innerWidth -= (labelWidth + imgui$getItemInnerSpacingX());
				} 
			}
		}else {
			if(hasVerticalScrollBar()) {
				innerWidth -= imgui$getScrollbarSize(); //imgui_wigets.cpp#L3922
			}
		}
		if((inputTextFlags & imgui$getNoHorizontalScrollFlag()) == 0) { //imgui_wigets.cpp#L4528
			final float scrollIncrementX = innerWidth * 0.25f;
			final float visibleWidth = innerWidth - imgui$getFramePaddingX();
			if(cursorOffsetX < internalScrollX) {
				internalScrollX = (float) Math.floor(Math.max(0.0f, cursorOffsetX - scrollIncrementX));
			}else if((cursorOffsetX - visibleWidth) >= internalScrollX) {
				internalScrollX = (float) Math.floor(cursorOffsetX - visibleWidth + scrollIncrementX);
			}
		}else {
			internalScrollX = 0.0f;
		}
	}
	
	private void splitLines(String text) {
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
	
	protected abstract int imgui$getID(String label);
	protected abstract int imgui$getFontSize();
	protected abstract float imgui$getLabelWidth(String label);
	protected abstract float imgui$getTextWidth(String text);
	protected abstract float imgui$getInnerWidth();
	protected abstract Rectangle imgui$getBounds();
	protected abstract float imgui$getFramePaddingX();
	protected abstract float imgui$getFramePaddingY();
	protected abstract float imgui$getScrollX();
	protected abstract float imgui$getScrollY();
	protected abstract float imgui$getItemInnerSpacingX();
	protected abstract float imgui$getScrollbarSize();
	protected abstract int imgui$getNoHorizontalScrollFlag();
	protected abstract boolean hasVerticalScrollBar();
}
