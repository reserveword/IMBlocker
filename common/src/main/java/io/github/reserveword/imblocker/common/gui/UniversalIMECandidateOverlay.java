package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.accessor.ImGuiGraphicsAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class UniversalIMECandidateOverlay {
	private static final UniversalIMECandidateOverlay INSTANCE = new UniversalIMECandidateOverlay();
	
	private static final int FOCUSED_COLOR = -986896;
	private static final int INDICATOR_COLOR = -11360971;
	private static final int TEXT_COLOR = -16777216;
	
	private final Font font;
	
	private int caretX;
	private int caretY;
	private int inputHeight;
	
	private String displayText;
	private int selectedStartIndex;
	private int selectedEndIndex;
	private boolean candidateListUpdated = false;
	
	private int displayTextWidth;
	private int selectedRenderX1;
	private int selectedRenderX2;
	private Rectangle overlayBounds = Rectangle.EMPTY;

	private UniversalIMECandidateOverlay() {
		this.font = Minecraft.getInstance().font;
	}
	
	public void updateCaretPosition(CaretInfo caretInfo) {
		this.caretX = caretInfo.caretX();
		this.caretY = caretInfo.caretY();
		this.inputHeight = caretInfo.inputHeight();
		updateCandidateArea();
	}
	
	public void candidateListUpdated(String[] selectedPageCandidates, int selectedIndex) {
		if(selectedPageCandidates != null) {
			StringBuilder displayTextBuilder = new StringBuilder();
			for(int i = 0; i < selectedPageCandidates.length; i++) {
				if(i == selectedIndex) {
					selectedStartIndex = displayTextBuilder.length();
				}
				displayTextBuilder.append(" " + (i + 1) + " " + selectedPageCandidates[i] + "  ");
				if(i == selectedIndex) {
					selectedEndIndex = displayTextBuilder.length();
				}
			}
			displayText = displayTextBuilder.toString();
			
			if(FocusManager.isMinecraftContextFocused()) {
				displayTextWidth = font.width(displayText);
				selectedRenderX1 = font.width(displayText.substring(0, selectedStartIndex));
				selectedRenderX2 = font.width(displayText.substring(0, selectedEndIndex));
				updateCandidateArea();
			}else {
				candidateListUpdated = true;
			}
		}else {
			displayText = null;
			candidateListUpdated = false;
		}
	}
	
	private void updateCandidateArea() {
		FocusableObject focusOwner = FocusManager.getFocusOwner();
		if(focusOwner != null && displayText != null) {
			int containerFontSize;
			double containerGuiScale;
			Rectangle candidateBorder;
			if(focusOwner instanceof FocusableWidget focusedWidget) {
				containerFontSize = focusedWidget.getFocusContainer().getFontHeight();
				containerGuiScale = focusedWidget.getFocusContainer().getGuiScale();
				candidateBorder = focusedWidget.getFocusContainer().getBoundsAbs();
			}else {
				containerFontSize = focusOwner.getFontHeight();
				containerGuiScale = focusOwner.getGuiScale();
				candidateBorder = focusOwner.getBoundsAbs();
			}
			
			int candidateX = caretX, 
					candidateY = (int) (caretY + inputHeight + (containerFontSize + 12) * containerGuiScale),
					candidateWidth = (int) (displayTextWidth * containerGuiScale),
					candidateHeight = (int) (containerFontSize * containerGuiScale);
			if(candidateX + candidateWidth > candidateBorder.width()) {
				candidateX = Math.max((int) (-selectedRenderX1 * containerGuiScale), candidateBorder.width() - candidateWidth);
			}
			if(candidateY + candidateHeight > candidateBorder.height()) {
				if(caretY + inputHeight + 5 * containerGuiScale + candidateHeight <= candidateBorder.height()) {
					candidateY = (int) (caretY - (4 + containerFontSize) * containerGuiScale);
				}else {
					candidateY = (int) (caretY - (6 + containerFontSize) * 2 * containerGuiScale);
				}
			}
			
			if(FocusManager.isMinecraftContextFocused()) {
				overlayBounds = new Rectangle(1.0 / containerGuiScale, candidateX, candidateY, candidateWidth, candidateHeight);
			}else{
				overlayBounds = new Rectangle(candidateX, candidateY, candidateWidth, candidateHeight);
			}
		}
	}
	
	public void renderOnMinecraftSurface(GuiGraphicsExtractor graphics) {
		if(displayText == null) {
			return;
		}
		
		graphics.fill(overlayBounds.x() - 4, overlayBounds.y() - 4, 
				overlayBounds.x() + overlayBounds.width() + 4, overlayBounds.y() + overlayBounds.height() + 4, -1);
		graphics.fill(overlayBounds.x() + selectedRenderX1, overlayBounds.y() - 2, 
				overlayBounds.x() + selectedRenderX2, overlayBounds.y() + overlayBounds.height() + 2, FOCUSED_COLOR);
		graphics.fill(overlayBounds.x() + selectedRenderX1, overlayBounds.y(), 
				overlayBounds.x() + selectedRenderX1 + 1, overlayBounds.y() + overlayBounds.height(), INDICATOR_COLOR);
		graphics.text(font, displayText, overlayBounds.x(), overlayBounds.y(), TEXT_COLOR, false);
	}
	
	public void renderOnImGuiSurface(ImGuiGraphicsAccessor graphics) {
		if(displayText == null) {
			return;
		}
		
		if(candidateListUpdated) {
			displayTextWidth = (int) graphics.getTextWidth(displayText);
			selectedRenderX1 = (int) graphics.getTextWidth(displayText.substring(0, selectedStartIndex));
			selectedRenderX2 = (int) graphics.getTextWidth(displayText.substring(0, selectedEndIndex));
			updateCandidateArea();
			candidateListUpdated = false;
		}

		graphics.addRectFilled(overlayBounds.x() - 4, overlayBounds.y() - 4, 
				overlayBounds.x() + overlayBounds.width() + 4, overlayBounds.y() + overlayBounds.height() + 4, 
				graphics.getColorU32(1, 1, 1, 1));
		graphics.addRectFilled(overlayBounds.x() + selectedRenderX1, overlayBounds.y() - 2, 
				overlayBounds.x() + selectedRenderX2, overlayBounds.y() + overlayBounds.height() + 2, 
				graphics.getColorU32i(0xFFF0F0F0));
		graphics.addRectFilled(overlayBounds.x() + selectedRenderX1, overlayBounds.y(), 
				overlayBounds.x() + selectedRenderX1 + 2, overlayBounds.y() + overlayBounds.height(), 
				graphics.getColorU32i(0xFF35A552));
		graphics.addText(overlayBounds.x(), overlayBounds.y(), graphics.getColorU32(0, 0, 0, 1), displayText);
	}
	
	public static UniversalIMECandidateOverlay getInstance() {
		return INSTANCE;
	}
}
