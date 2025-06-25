package io.github.reserveword.imblocker.common.gui;

import imgui.ImGuiIO;

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
				FocusContainer.IMGUI.requestFocus(GenericAxiomWidget.getInstance());
			}else {
				FocusContainer.IMGUI.cancelFocus();
			}
		}
	}
	
	public static void createInstance(ImGuiIO axiomGuiAccessor) {
		instance = new AxiomGuiMonitor(axiomGuiAccessor);
	}
	
	public static AxiomGuiMonitor getInstance() {
		return instance;
	}
}
