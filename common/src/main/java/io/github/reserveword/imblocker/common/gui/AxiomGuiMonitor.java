package io.github.reserveword.imblocker.common.gui;

import com.moulberry.axiom.editor.EditorUI;

import imgui.moulberry92.ImGuiIO;

public class AxiomGuiMonitor {
	
	private static AxiomGuiMonitor instance;

	private final ImGuiIO axiomGuiAccessor;
	
	private boolean axiomGuiCaptureKeyboard = false;
	private boolean axiomTextFieldFocused = false;

	public AxiomGuiMonitor(ImGuiIO axiomGuiAccessor) {
		this.axiomGuiAccessor = axiomGuiAccessor;
	}

	public final void tick() {
		boolean isAxiomGuiFocused = axiomGuiAccessor.getWantCaptureKeyboard();
		boolean isAxiomTextFieldFocused = axiomGuiAccessor.getWantTextInput();
		
		if(axiomGuiCaptureKeyboard != isAxiomGuiFocused) {
			axiomGuiCaptureKeyboard = isAxiomGuiFocused;
			FocusManager.requestFocus(isAxiomGuiFocused ? 
					FocusContainer.IMGUI : FocusContainer.MINECRAFT);
		}
		
		if(axiomTextFieldFocused != isAxiomTextFieldFocused) {
			axiomTextFieldFocused = isAxiomTextFieldFocused;
			if(isAxiomTextFieldFocused) {
				FocusContainer.IMGUI.setPreferredState(true);
			}else {
				FocusContainer.IMGUI.clearFocus();
				FocusContainer.IMGUI.setPreferredState(false);
			}
		}
	}
	
	public boolean isAxiomEditorShowing() {
		return EditorUI.isEnabled();
	}
	
	public int getGameContentOffsetX() {
		return EditorUI.frameX;
	}
	
	public int getGameContentOffsetY() {
		return EditorUI.frameY;
	}
	
	public static void createInstance() {
		instance = new AxiomGuiMonitor(EditorUI.imGuiIO);
	}
	
	public static AxiomGuiMonitor getInstance() {
		return instance;
	}
}
