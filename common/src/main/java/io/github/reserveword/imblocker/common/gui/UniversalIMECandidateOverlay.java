package io.github.reserveword.imblocker.common.gui;

import imgui.moulberry92.ImDrawList;
import imgui.moulberry92.ImGui;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

public class UniversalIMECandidateOverlay {
	private static final UniversalIMECandidateOverlay INSTANCE = new UniversalIMECandidateOverlay();
	
	private static final int FOCUSED_COLOR = -986896;
	private static final int INDICATOR_COLOR = -11360971;
	private static final int TEXT_COLOR = -16777216;
	
	private int caretX;
	private int caretY;
	
	private String displayText;
	private int selectedStartIndex;
	private int selectedEndIndex;
	private boolean candidateListUpdated = false;
	
	private int displayTextWidth;
	private int selectedRenderX1;
	private int selectedRenderX2;
	private Rectangle ingameOverlayBounds = Rectangle.EMPTY;
	private Rectangle overlayBounds = Rectangle.EMPTY;

	private UniversalIMECandidateOverlay() {}
	
	public void updateCaretPosition(int caretX, int caretY) {
		this.caretX = caretX;
		this.caretY = caretY;
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
			
			if(FocusManager.getFocusedContainer() == FocusContainer.MINECRAFT) {
				displayTextWidth = MinecraftClientAccessor.INSTANCE.getStringWidth(displayText);
				selectedRenderX1 = MinecraftClientAccessor.INSTANCE.getStringWidth(displayText.substring(0, selectedStartIndex));
				selectedRenderX2 = MinecraftClientAccessor.INSTANCE.getStringWidth(displayText.substring(0, selectedEndIndex));
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
			int widgetFontSize = focusOwner.getFontHeight();
			double widgetGuiScale = focusOwner.getGuiScale();
			int containerFontSize;
			double containerGuiScale;
			Rectangle candidateBorder;
			if(focusOwner instanceof FocusableWidget) {
				FocusableWidget focusedWidget = (FocusableWidget) focusOwner;
				containerFontSize = focusedWidget.getFocusContainer().getFontHeight();
				containerGuiScale = focusedWidget.getFocusContainer().getGuiScale();
				candidateBorder = focusedWidget.getFocusContainer().getBoundsAbs();
			}else {
				containerFontSize = widgetFontSize;
				containerGuiScale = widgetGuiScale;
				candidateBorder = focusOwner.getBoundsAbs();
			}
			
			int candidateX = caretX, 
					candidateY = (int) (caretY + widgetFontSize * widgetGuiScale + (containerFontSize + 12) * containerGuiScale),
					candidateWidth = (int) (displayTextWidth * containerGuiScale),
					candidateHeight = (int) (containerFontSize * containerGuiScale);
			if(candidateX + candidateWidth > candidateBorder.width()) {
				candidateX = Math.max((int) (-selectedRenderX1 * containerGuiScale), candidateBorder.width() - candidateWidth);
			}
			if(candidateY + candidateHeight > candidateBorder.height()) {
				if(caretY + widgetFontSize * widgetGuiScale + 5 * containerGuiScale + candidateHeight <= candidateBorder.height()) {
					candidateY = (int) (caretY - (4 + containerFontSize) * containerGuiScale);
				}else {
					candidateY = (int) (caretY - (6 + containerFontSize) * 2 * containerGuiScale);
				}
			}
			
			ingameOverlayBounds = new Rectangle(1.0 / containerGuiScale, candidateX, candidateY, candidateWidth, candidateHeight);
			candidateX += candidateBorder.x();
			candidateY += candidateBorder.y();
			overlayBounds = new Rectangle(candidateX, candidateY, candidateWidth, candidateHeight);
		}
	}
	
	public void renderOnMinecraftSurface(MinecraftRenderApi graphics) {
		if(displayText == null) {
			return;
		}
		
		graphics.fillRect(ingameOverlayBounds.x() - 4, ingameOverlayBounds.y() - 4, 
				ingameOverlayBounds.x() + ingameOverlayBounds.width() + 4, ingameOverlayBounds.y() + ingameOverlayBounds.height() + 4, -1);
		graphics.fillRect(ingameOverlayBounds.x() + selectedRenderX1, ingameOverlayBounds.y() - 2, 
				ingameOverlayBounds.x() + selectedRenderX2, ingameOverlayBounds.y() + ingameOverlayBounds.height() + 2, FOCUSED_COLOR);
		graphics.fillRect(ingameOverlayBounds.x() + selectedRenderX1, ingameOverlayBounds.y(), 
				ingameOverlayBounds.x() + selectedRenderX1 + 1, ingameOverlayBounds.y() + ingameOverlayBounds.height(), INDICATOR_COLOR);
		graphics.drawText(displayText, ingameOverlayBounds.x(), ingameOverlayBounds.y(), TEXT_COLOR);
	}
	
	public void renderOnImGuiSurface(ImDrawList graphics) {
		if(displayText == null) {
			return;
		}
		
		if(candidateListUpdated) {
			displayTextWidth = (int) ImGui.calcTextSize(displayText).x;
			selectedRenderX1 = (int) ImGui.calcTextSize(displayText.substring(0, selectedStartIndex)).x;
			selectedRenderX2 = (int) ImGui.calcTextSize(displayText.substring(0, selectedEndIndex)).x;
			updateCandidateArea();
			candidateListUpdated = false;
		}

		graphics.addRectFilled(overlayBounds.x() - 4, overlayBounds.y() - 4, 
				overlayBounds.x() + overlayBounds.width() + 4, overlayBounds.y() + overlayBounds.height() + 4, 
				ImGui.getColorU32(1, 1, 1, 1));
		graphics.addRectFilled(overlayBounds.x() + selectedRenderX1, overlayBounds.y() - 2, 
				overlayBounds.x() + selectedRenderX2, overlayBounds.y() + overlayBounds.height() + 2, 
				ImGui.getColorU32i(0xFFF0F0F0));
		graphics.addRectFilled(overlayBounds.x() + selectedRenderX1, overlayBounds.y(), 
				overlayBounds.x() + selectedRenderX1 + 2, overlayBounds.y() + overlayBounds.height(), 
				ImGui.getColorU32i(0xFF35A552));
		graphics.addText(overlayBounds.x(), overlayBounds.y(), ImGui.getColorU32(0, 0, 0, 1), displayText);
	}
	
	public static UniversalIMECandidateOverlay getInstance() {
		return INSTANCE;
	}
}
