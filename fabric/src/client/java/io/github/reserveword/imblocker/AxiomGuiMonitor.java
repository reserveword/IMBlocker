package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.gui.AxiomGuiAccessor;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.GenericAxiomWidget;

public class AxiomGuiMonitor {
	
	private static boolean axiomGuiCaptureKeyboard = false;
    private static boolean axiomTextFieldFocused = false;
	
	public static void tick() {
		AxiomGuiAccessor axiomGuiAccessor = AxiomGuiAccessor.instance;
    	if(axiomGuiAccessor != null) {
	    	boolean isAxiomGuiFocused = axiomGuiAccessor.isCaptureKeyboard();
			boolean isAxiomTextFieldFocused = axiomGuiAccessor.isTextFieldFocused();
			
			if(axiomGuiCaptureKeyboard != isAxiomGuiFocused) {
				axiomGuiCaptureKeyboard = isAxiomGuiFocused;
				FocusManager.setFocusedContainer(isAxiomGuiFocused ? 
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
	}
}
