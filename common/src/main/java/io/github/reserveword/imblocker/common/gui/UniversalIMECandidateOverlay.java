package io.github.reserveword.imblocker.common.gui;

import imgui.ImDrawList;
import imgui.ImGui;
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
	
	private String displayText;
	private int selectedStartIndex;
	private int selectedEndIndex;
	private boolean candidateListUpdated = false;
	
	private int displayTextWidth;
	private int selectedRenderX1;
	private int selectedRenderX2;
	private Rectangle ingameOverlayBounds = Rectangle.EMPTY;
	private Rectangle overlayBounds = Rectangle.EMPTY;

	private UniversalIMECandidateOverlay() {
		this.font = Minecraft.getInstance().font;
	}
	
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
			double guiScale = focusOwner.getGuiScale();
			int fontSize = focusOwner.getFontHeight() + 1;
			Rectangle candidateBorder = focusOwner instanceof FocusableWidget ?
					((FocusableWidget) focusOwner).getFocusContainer().getBoundsAbs() : focusOwner.getBoundsAbs();
			int candidateX = caretX, candidateY = (int) (caretY + (fontSize + 5) * 2 * guiScale),
					candidateWidth = (int) (displayTextWidth * guiScale),
					candidateHeight = (int) (fontSize * guiScale);
			if(candidateX + candidateWidth > candidateBorder.width()) {
				candidateX = Math.max((int) (-selectedRenderX1 * guiScale), candidateBorder.width() - candidateWidth);
			}
			if(candidateY > candidateBorder.height()) {
				if((caretY + (fontSize + 4) * guiScale) <= candidateBorder.height()) {
					candidateY = (int) (caretY - (3 + fontSize) * guiScale);
				}else {
					candidateY = (int) (caretY - (6 + fontSize) * 2 * guiScale);
				}
			}
			
			ingameOverlayBounds = new Rectangle(1.0 / guiScale, candidateX, candidateY, candidateWidth, candidateHeight);
			candidateX += candidateBorder.x();
			candidateY += candidateBorder.y();
			overlayBounds = new Rectangle(candidateX, candidateY, candidateWidth, candidateHeight);
		}
	}
	
	public void renderOnMinecraftSurface(GuiGraphicsExtractor graphics) {
		if(displayText == null) {
			return;
		}
		
		graphics.fill(ingameOverlayBounds.x() - 4, ingameOverlayBounds.y() - 4, 
				ingameOverlayBounds.x() + ingameOverlayBounds.width() + 4, ingameOverlayBounds.y() + ingameOverlayBounds.height() + 4, -1);
		graphics.fill(ingameOverlayBounds.x() + selectedRenderX1, ingameOverlayBounds.y() - 2, 
				ingameOverlayBounds.x() + selectedRenderX2, ingameOverlayBounds.y() + ingameOverlayBounds.height() + 2, FOCUSED_COLOR);
		graphics.fill(ingameOverlayBounds.x() + selectedRenderX1, ingameOverlayBounds.y(), 
				ingameOverlayBounds.x() + selectedRenderX1 + 1, ingameOverlayBounds.y() + ingameOverlayBounds.height(), INDICATOR_COLOR);
		graphics.text(font, displayText, ingameOverlayBounds.x(), ingameOverlayBounds.y(), TEXT_COLOR, false);
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
