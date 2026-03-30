package io.github.reserveword.imblocker.common.gui;

import imgui.ImGuiIO;
import io.github.reserveword.imblocker.common.ReflectionUtil;

public class AxiomGuiMonitor {
	
	private static AxiomGuiMonitor instance;

	private final ImGuiIO axiomGuiAccessor;
	
	private boolean axiomGuiCaptureKeyboard = false;
	private boolean axiomTextFieldFocused = false;
	
	private final Class<?> axiomEditorUIClass;

	public AxiomGuiMonitor(ImGuiIO axiomGuiAccessor, Class<?> axiomEditorUIClass) {
		this.axiomGuiAccessor = axiomGuiAccessor;
		this.axiomEditorUIClass = axiomEditorUIClass;
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
		return ReflectionUtil.getFieldValue(axiomEditorUIClass, null, boolean.class, "enabled");
	}
	
	public int getGameContentOffsetX() {
		return ReflectionUtil.getFieldValue(axiomEditorUIClass, null, int.class, "frameX");
	}
	
	public int getGameContentOffsetY() {
		return ReflectionUtil.getFieldValue(axiomEditorUIClass, null, int.class, "frameY");
	}
	
	public static void createInstance(ImGuiIO axiomGuiAccessor, Class<?> axiomEditorUIClass) {
		instance = new AxiomGuiMonitor(axiomGuiAccessor, axiomEditorUIClass);
	}
	
	public static AxiomGuiMonitor getInstance() {
		return instance;
	}
}
