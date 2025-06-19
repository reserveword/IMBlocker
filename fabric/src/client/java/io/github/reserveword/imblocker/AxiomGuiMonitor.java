package io.github.reserveword.imblocker;

import imgui.ImGuiIO;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.GenericAxiomWidget;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.StartTick;
import net.minecraft.client.MinecraftClient;

public class AxiomGuiMonitor implements StartTick {
	
	private final ImGuiIO axiomGuiAccessor;
	
	private boolean axiomGuiCaptureKeyboard = false;
	private boolean axiomTextFieldFocused = false;

	public AxiomGuiMonitor(ImGuiIO axiomGuiAccessor) {
		this.axiomGuiAccessor = axiomGuiAccessor;
	}

	@Override
	public void onStartTick(MinecraftClient client) {
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
}
