package io.github.reserveword.imblocker.common.gui;

import java.util.Objects;

import imgui.moulberry92.ImDrawList;
import imgui.moulberry92.ImGui;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

public class UniversalIMEPreeditOverlay {
	private static final UniversalIMEPreeditOverlay INSTANCE = new UniversalIMEPreeditOverlay();
	
	private static final int BORDER_COLOR = -11731200;
	private static final int TEXT_COLOR = -16777216;
	
	private final long initTimeMs;
	
	private int caretX;
	private int caretY;
	private int inputHeight;
	
	private String preEditText;
	private int preEditCaretPos;
	private boolean preEditContentUpdated = false;
	
	private int preEditTextWidth;
	private int preEditCaretRenderX;
	private Rectangle overlayBounds = Rectangle.EMPTY;

	private UniversalIMEPreeditOverlay() {
		this.initTimeMs = System.currentTimeMillis();
	}
	
	public void updateCaretPosition(int caretX, int caretY) {
		this.caretX = caretX;
		this.caretY = caretY;
		FocusableObject focusOwner = FocusManager.getFocusOwner();
		this.inputHeight = focusOwner != null ? (int) (focusOwner.getFontHeight() * focusOwner.getGuiScale()) : 0;
		updatePreeditArea();
	}

	public void updateCaretPosition(CaretInfo caretInfo) {
		this.caretX = caretInfo.caretX();
		this.caretY = caretInfo.caretY();
		this.inputHeight = caretInfo.inputHeight();
		updatePreeditArea();
	}
	
	public void preeditContentUpdated(String compositionString, int caretPosition) {
		if(compositionString != null) {
			if(!Objects.equals(preEditText, compositionString) || (preEditCaretPos != caretPosition)) {
				preEditText = compositionString;
				preEditCaretPos = caretPosition;
				
				if(FocusManager.isMinecraftContextFocused()) {
					preEditTextWidth = MinecraftClientAccessor.INSTANCE.getStringWidth(preEditText);
					preEditCaretRenderX = MinecraftClientAccessor.INSTANCE.getStringWidth(preEditText.substring(0, preEditCaretPos));
					updatePreeditArea();
				}else {
					preEditContentUpdated = true;
				}
			}
		}else {
			preEditText = null;
			preEditContentUpdated = false;
		}
	}
	
	private void updatePreeditArea() {
		FocusableObject focusOwner = FocusManager.getFocusOwner();
		if(focusOwner != null && preEditText != null) {
			int containerFontSize;
			double containerGuiScale;
			Rectangle compositionBorder;
			if(focusOwner instanceof FocusableWidget) {
				FocusableWidget focusedWidget = (FocusableWidget) focusOwner;
				containerFontSize = focusedWidget.getFocusContainer().getFontHeight();
				containerGuiScale = focusedWidget.getFocusContainer().getGuiScale();
				compositionBorder = focusedWidget.getFocusContainer().getBoundsAbs();
			}else {
				containerFontSize = focusOwner.getFontHeight();
				containerGuiScale = focusOwner.getGuiScale();
				compositionBorder = focusOwner.getBoundsAbs();
			}

			int paddedInputHeight = (int) (inputHeight + 5 * containerGuiScale);
			int compositionX = caretX, compositionY = caretY + paddedInputHeight,
					compositionWidth = (int) (preEditTextWidth * containerGuiScale),
					compositionHeight = (int) (containerFontSize * containerGuiScale);
			if(compositionX + compositionWidth > compositionBorder.width()) {
				compositionX = compositionBorder.width() - compositionWidth;
			}
			if(compositionY + compositionHeight > compositionBorder.height()) {
				compositionY = (int) (caretY - (6 + containerFontSize) * containerGuiScale);
			}
			
			if(FocusManager.isMinecraftContextFocused()) {
				overlayBounds = new Rectangle(1.0 / containerGuiScale, compositionX, compositionY, compositionWidth, compositionHeight);			
			}else {
				overlayBounds = new Rectangle(compositionX, compositionY, compositionWidth, compositionHeight);
			}
			
			if(!IMBlockerConfig.INSTANCE.isIngameIMEEnabled()) {
				int scaledMargin = (int) (2 * containerGuiScale), 
						compositionMinY = Math.min(caretY, compositionY);
				compositionX += compositionBorder.x();
				compositionY += compositionBorder.y();
				compositionMinY += compositionBorder.y();
				IMManager.setPreeditCursorRectangle(
						compositionX - scaledMargin, compositionMinY - scaledMargin,
						compositionWidth + scaledMargin * 2, compositionHeight + paddedInputHeight + scaledMargin * 2);
			}
		}
	}

	public void renderOnMinecraftSurface(MinecraftRenderApi graphics) {
		if(preEditText == null) {
			return;
		}
		
		graphics.fillRect(overlayBounds.x() - 5, overlayBounds.y() - 5, 
				overlayBounds.x() + overlayBounds.width() + 5, overlayBounds.y() + overlayBounds.height() + 5, BORDER_COLOR);
		graphics.fillRect(overlayBounds.x() - 4, overlayBounds.y() - 4, 
				overlayBounds.x() + overlayBounds.width() + 4, overlayBounds.y() + overlayBounds.height() + 4, -1);
		graphics.drawText(preEditText, overlayBounds.x(), overlayBounds.y(), TEXT_COLOR);
		if (isCursorVisible()) {
			graphics.fillRect(overlayBounds.x() + preEditCaretRenderX, overlayBounds.y() - 1, 
					overlayBounds.x() + preEditCaretRenderX + 1, overlayBounds.y() + 10, TEXT_COLOR);
		}
	}
	
	public void renderOnImGuiSurface(ImDrawList graphics) {
		if(preEditText == null) {
			return;
		}
		
		if(preEditContentUpdated) {
			preEditTextWidth = (int) ImGui.calcTextSize(preEditText).x;
			preEditCaretRenderX = (int) ImGui.calcTextSize(preEditText.substring(0, preEditCaretPos)).x;
			updatePreeditArea();
			preEditContentUpdated = false;
		}
		
		graphics.addRectFilled(
				overlayBounds.x() - 4, overlayBounds.y() - 4, 
				overlayBounds.x() + overlayBounds.width() + 4, overlayBounds.y() + overlayBounds.height() + 4, 
				ImGui.getColorU32(1, 1, 1, 1));
		graphics.addText(overlayBounds.x(), overlayBounds.y(), ImGui.getColorU32(0, 0, 0, 1), preEditText);
		if(isCursorVisible()) {
			graphics.addRectFilled(
					overlayBounds.x() + preEditCaretRenderX, overlayBounds.y(), 
					overlayBounds.x() + preEditCaretRenderX + 2, overlayBounds.y() + overlayBounds.height(), 
					ImGui.getColorU32(0, 0, 0, 1));
		}
	}
	
	private boolean isCursorVisible() {
		return (System.currentTimeMillis() - initTimeMs) / 300L % 2L == 0L;
	}
	
	public static UniversalIMEPreeditOverlay getInstance() {
		return INSTANCE;
	}
}
