package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMManager;

/**
 * <p>This interface represents an <b>effective input entry</b> in Minecraft
 * context. An instance of this class contains all key values retrieved from
 * its corresponding Minecraft input context (e.g. screens, widgets) which are
 * used to determine the properties of IME.
 * 
 * <p>This class is a base part of <b>IMBlocker's focus management system</b>.
 * 
 * @see FocusManager
 * @see FocusContainer
 * @see FocusableWidget
 * @author LitnhJacuzzi
 * @since 5.1.0
 */
public interface FocusableObject {

	/**
	 * <p>Called from parent focus transfer node, deliver the focus to this
	 * focusable element.
	 * 
	 * <p>If this element is an instance of {@code FocusContainer}, the focus
	 * may be transferred to its {@code focusedWidget} if present.
	 * 
	 * <p><b>This method will modify the focus transfer path and must be called
	 * with proper validation.</b>
	 */
	default void deliverFocus() {
		FocusManager.setFocusOwner(this);
		updateIMState();
	}
	
	/**
	 * <p>Notify this element to lose its focus. <b>This method should be called
	 * before assigning new {@code focusOwner}</b>.
	 * 
	 * <p>This method is originally designed to sync the focus state of its
	 * corresponding Minecraft input context, however it's not practical to do
	 * that because of their chaotic architectures.
	 */
	default void lostFocus() {
		FocusManager.setFocusOwner(null);
	}
	
	/**
	 * Whether this focusable element is the <b>REAL</b> ultimate destination of
	 * keyboard inputs.
	 */
	default boolean isTrulyFocused() {
		return FocusManager.getFocusOwner() == this;
	}
	
	/**
	 * <p>Update the states of IME with this element's preferred states.
	 * 
	 * <p><b>This method can only be called if this element {@code isTrulyFocused}</b>.
	 */
	default void updateIMState() {
		boolean shouldEnableIME = getPreferredState();
		IMManager.setState(shouldEnableIME);
		if(shouldEnableIME) {
			IMManager.updateCompositionWindowPos();
			IMManager.updateCompositionFontSize();
			IMManager.setEnglishState(getPreferredEnglishState());
		}
	}
	
	/**
	 * <p>Update the English state (conversion status on Windows platform) of IME
	 * with this element's preferred English state.
	 * 
	 * <p><b>This method can only be called if this element {@code isTrulyFocused}</b>.
	 */
	default void updateEnglishState() {
		if(getPreferredState()) {
			IMManager.setEnglishState(getPreferredEnglishState());
		}
	}
	
	/**
	 * Get the expected IME state of this element.
	 * 
	 * @return {@code true} if this element wishes to enable the IME, {@code false}
	 * if this element wishes to disable the IME.
	 */
	boolean getPreferredState();
	
	default boolean getPreferredEnglishState() {
		return false;
	}
	
	/**
	 * Get the bounds of this element relative to the game window. Used to calculate
	 * the position of IME's composition window.
	 */
	Rectangle getBoundsAbs();
	
	/**
	 * Get the caret position (i.e. the position that insert characters) of this element
	 * relative to itself. Used to calculate the position of IME's composition window.
	 */
	Point getCaretPos();
	
	default int getFontHeight() {
		return 8;
	}
	
	double getGuiScale();
}
