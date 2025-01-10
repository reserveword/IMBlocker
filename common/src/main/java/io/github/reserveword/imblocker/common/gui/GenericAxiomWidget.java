package io.github.reserveword.imblocker.common.gui;

public class GenericAxiomWidget implements FocusableWidget {
	
	private static final GenericAxiomWidget INSTANCE = new GenericAxiomWidget();
	
	@Override
	public final FocusContainer getFocusContainer() {
		return FocusContainer.IMGUI;
	}

	@Override
	public boolean isWidgetEditable() {
		return true;
	}
	
	public static GenericAxiomWidget getInstance() {
		return INSTANCE;
	}
}
