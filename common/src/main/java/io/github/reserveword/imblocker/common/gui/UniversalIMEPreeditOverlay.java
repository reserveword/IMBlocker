package io.github.reserveword.imblocker.common.gui;

import imgui.ImDrawList;
import imgui.ImGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class UniversalIMEPreeditOverlay {
	private static final UniversalIMEPreeditOverlay INSTANCE = new UniversalIMEPreeditOverlay();
	
	private static final int BORDER_COLOR = -11731200;
	private static final int TEXT_COLOR = -16777216;
	
	private final long initTimeMs;
	
	private final Font font;
	
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
		this.font = Minecraft.getInstance().font;
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
				preEditTextWidth = font.width(preEditText);
				preEditCaretRenderX = font.width(preEditText.substring(0, preEditCaretPos));
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
			double guiScale = focusOwner.getGuiScale();
			int fontSize = focusOwner.getFontHeight() + 1;
			Rectangle compositionBorder = focusOwner instanceof FocusableWidget ?
					((FocusableWidget) focusOwner).getFocusContainer().getBoundsAbs() : focusOwner.getBoundsAbs();
			int compositionX = caretX, compositionY = (int) (caretY + (fontSize + 4) * guiScale),
					compositionWidth = (int) (preEditTextWidth * guiScale),
					compositionHeight = (int) (fontSize * guiScale);
			if(compositionX + compositionWidth > compositionBorder.width()) {
				compositionX = compositionBorder.width() - compositionWidth;
			}
			if(compositionY > compositionBorder.height()) {
				compositionY = (int) (caretY - (6 + fontSize) * guiScale);
			}
			
			ingameOverlayBounds = new Rectangle(1.0 / guiScale, compositionX, compositionY, compositionWidth, compositionHeight);			
			compositionX += compositionBorder.x();
			compositionY += compositionBorder.y();
			overlayBounds = new Rectangle(compositionX, compositionY, compositionWidth, compositionHeight);
		}
	}

	public void renderOnMinecraftSurface(GuiGraphics graphics) {
		if(preEditText == null) {
			return;
		}
		
		graphics.fill(ingameOverlayBounds.x() - 5, ingameOverlayBounds.y() - 5, 
				ingameOverlayBounds.x() + ingameOverlayBounds.width() + 5, ingameOverlayBounds.y() + ingameOverlayBounds.height() + 5, BORDER_COLOR);
		graphics.fill(ingameOverlayBounds.x() - 4, ingameOverlayBounds.y() - 4, 
				ingameOverlayBounds.x() + ingameOverlayBounds.width() + 4, ingameOverlayBounds.y() + ingameOverlayBounds.height() + 4, -1);
		graphics.drawString(font, preEditText, ingameOverlayBounds.x(), ingameOverlayBounds.y(), TEXT_COLOR, false);
		if (isCursorVisible()) {
			graphics.fill(ingameOverlayBounds.x() + preEditCaretRenderX, ingameOverlayBounds.y() - 1, 
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
				overlayBounds.x() - 2, overlayBounds.y() - 2, 
				overlayBounds.x() + overlayBounds.width() + 2, overlayBounds.y() + overlayBounds.height() + 2, 
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
