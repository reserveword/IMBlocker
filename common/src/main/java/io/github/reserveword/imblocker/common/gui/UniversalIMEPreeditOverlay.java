package io.github.reserveword.imblocker.common.gui;

import imgui.moulberry92.ImDrawList;
import imgui.moulberry92.ImGui;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import xyz.rrtt217.HDRMod.compat.imblocker.IMManagerLinuxEnhanced;

public class UniversalIMEPreeditOverlay {
	private static final UniversalIMEPreeditOverlay INSTANCE = new UniversalIMEPreeditOverlay();
	
	private static final int BORDER_COLOR = -11731200;
	private static final int TEXT_COLOR = -16777216;
	
	private final long initTimeMs;
	
	private int caretX;
	private int caretY;
	
	private String preEditText;
	private int preEditCaretPos;
	private boolean preEditContentUpdated = false;
	
	private int preEditTextWidth;
	private int preEditCaretRenderX;
	private Rectangle ingameOverlayBounds = Rectangle.EMPTY;
	private Rectangle overlayBounds = Rectangle.EMPTY;

	private UniversalIMEPreeditOverlay() {
		this.initTimeMs = System.currentTimeMillis();
	}

	public void updateCaretPosition(int caretX, int caretY) {
		this.caretX = caretX;
		this.caretY = caretY;
		updatePreeditArea();
	}
	
	public void preeditContentUpdated(String compositionString, int caretPosition) {
		if(compositionString != null) {
			preEditText = compositionString;
			preEditCaretPos = caretPosition;
			
			if(FocusManager.getFocusedContainer() == FocusContainer.MINECRAFT) {
				preEditTextWidth = MinecraftClientAccessor.INSTANCE.getStringWidth(preEditText);
				preEditCaretRenderX = MinecraftClientAccessor.INSTANCE.getStringWidth(preEditText.substring(0, preEditCaretPos));
				updatePreeditArea();
			}else {
				preEditContentUpdated = true;
			}	
		}else {
			preEditText = null;
			preEditContentUpdated = false;
		}
	}
	
	private void updatePreeditArea() {
		FocusableObject focusOwner = FocusManager.getFocusOwner();
		if(focusOwner != null && preEditText != null) {
			double widgetGuiScale = focusOwner.getGuiScale();
			int widgetFontSize = focusOwner.getFontHeight();
			int containerFontSize;
			double containerGuiScale;
			Rectangle compositionBorder;
			if(focusOwner instanceof FocusableWidget) {
				FocusableWidget focusedWidget = (FocusableWidget) focusOwner;
				containerFontSize = focusedWidget.getFocusContainer().getFontHeight();
				containerGuiScale = focusedWidget.getFocusContainer().getGuiScale();
				compositionBorder = focusedWidget.getFocusContainer().getBoundsAbs();
			}else {
				containerFontSize = widgetFontSize;
				containerGuiScale = widgetGuiScale;
				compositionBorder = focusOwner.getBoundsAbs();
			}

			int inputHeight = (int) (widgetFontSize * widgetGuiScale + 5 * containerGuiScale);
			int compositionX = caretX, compositionY = caretY + inputHeight,
					compositionWidth = (int) (preEditTextWidth * containerGuiScale),
					compositionHeight = (int) (containerFontSize * containerGuiScale);
			if(compositionX + compositionWidth > compositionBorder.width()) {
				compositionX = compositionBorder.width() - compositionWidth;
			}
			if(compositionY + compositionHeight > compositionBorder.height()) {
				compositionY = (int) (caretY - (6 + containerFontSize) * containerGuiScale);
			}
			
			ingameOverlayBounds = new Rectangle(1.0 / containerGuiScale, compositionX, compositionY, compositionWidth, compositionHeight);			
			compositionX += compositionBorder.x();
			compositionY += compositionBorder.y();
			overlayBounds = new Rectangle(compositionX, compositionY, compositionWidth, compositionHeight);
			
			if(IMManager.isEnhancedLinuxImplPresent()) {
				int scaledMargin = (int) (2 * containerGuiScale);
				IMManagerLinuxEnhanced.updatePreeditCursorRectanglePosition(
						compositionX - scaledMargin, Math.min(caretY, compositionY) - scaledMargin,
						compositionWidth + scaledMargin * 2, compositionHeight + inputHeight + scaledMargin * 2);
			}
		}
	}

	public void renderOnMinecraftSurface(MinecraftRenderApi graphics) {
		if(preEditText == null) {
			return;
		}
		
		graphics.fillRect(ingameOverlayBounds.x() - 5, ingameOverlayBounds.y() - 5, 
				ingameOverlayBounds.x() + ingameOverlayBounds.width() + 5, ingameOverlayBounds.y() + ingameOverlayBounds.height() + 5, BORDER_COLOR);
		graphics.fillRect(ingameOverlayBounds.x() - 4, ingameOverlayBounds.y() - 4, 
				ingameOverlayBounds.x() + ingameOverlayBounds.width() + 4, ingameOverlayBounds.y() + ingameOverlayBounds.height() + 4, -1);
		graphics.drawText(preEditText, ingameOverlayBounds.x(), ingameOverlayBounds.y(), TEXT_COLOR);
		if (isCursorVisible()) {
			graphics.fillRect(ingameOverlayBounds.x() + preEditCaretRenderX, ingameOverlayBounds.y() - 1, 
					ingameOverlayBounds.x() + preEditCaretRenderX + 1, ingameOverlayBounds.y() + 10, TEXT_COLOR);
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
