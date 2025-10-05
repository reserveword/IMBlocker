package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.MinecraftClientUtil;

/**
 * <p>This class represents the focus destination of the game window. The game
 * window must have a {@code FocusContainer} to deliver the focus, on receiving
 * the focus, this container will transfer it to its focus destination -
 * {@code focusedWidget} or directly hold it if the {@code focusedWidget} is
 * null.
 * 
 * <p>The corresponding Minecraft GUI context of a {@code FocusContainer} is all
 * non-textfield widgets, as they share the same IME preferences.
 * 
 * <p>This class is a base part of <b>IMBlocker's focus management system</b>.
 * 
 * @see FocusManager
 * @see FocusableWidget
 * @author LitnhJacuzzi
 * @since 5.1.0
 */
public abstract class FocusContainer implements FocusableObject {
	public static final FocusContainer MINECRAFT = new MinecraftFocusContext();
	public static final FocusContainer IMGUI = new ImGuiFocusContext();
	
	double guiScaleFactor = 1.0;
	
	boolean isFocused;
	boolean preferredState = false;
	
	FocusableWidget focusedWidget;
	
	FocusContainer(boolean defaultFocusState) {
		isFocused = defaultFocusState;
	}
	
	/**
	 * Request to change the focus destination of this container to given widget.
	 */
	public void requestFocus(FocusableWidget toFocus) {
		if(focusedWidget != toFocus) {
			switchFocus(toFocus);
		}
	}
	
	/**
	 * @see MinecraftFocusContext#locateRealFocus
	 */
	public void locateRealFocus() {}
	
	/**
	 * Request to remove the given widget from this container's focus candidates.
	 */
	public void removeFocus(FocusableWidget toRemove) {
		if(focusedWidget == toRemove) {
			restoreContainerFocus();
		}
	}
	
	/**
	 * Change the focus destination of this container to given widget.
	 */
	public void switchFocus(FocusableWidget toFocus) {
		if(focusedWidget != toFocus) {
			if(isFocused) {
				if(focusedWidget != null) {
					focusedWidget.lostFocus();
				}
				focusedWidget = toFocus;
				focusedWidget.deliverFocus();
			}else {
				focusedWidget = toFocus;
			}
		}
	}
	
	/**
	 * Clear all focus candidates in this container.
	 */
	public void clearFocus() {
		restoreContainerFocus();
	}
	
	/**
	 * @see MinecraftFocusContext#checkFocusCandidatesVisibility
	 */
	public void checkFocusCandidatesVisibility(long lastGameRenderTime) {}
	
	/**
	 * Assign the global focus owner to this container if no focus destination available.
	 */
	void restoreContainerFocus() {
		if(focusedWidget != null) {
			if(isFocused) {
				focusedWidget.lostFocus();
				FocusableObject.super.deliverFocus();
			}
			focusedWidget = null;
		}
	}
	
	@Override
	public void deliverFocus() {
		isFocused = true;
		if(focusedWidget != null) {
			focusedWidget.deliverFocus();
		}else {
			FocusableObject.super.deliverFocus();
		}
	}
	
	@Override
	public void lostFocus() {
		isFocused = false;
		if(focusedWidget != null) {
			focusedWidget.lostFocus();
		}else {
			FocusableObject.super.lostFocus();
		}
	}
	
	public void setPreferredState(boolean preferredState) {
		if(this.preferredState != preferredState) {
			this.preferredState = preferredState;
			if(isTrulyFocused()) {
				updateIMState();
			}
		}
	}

	@Override
	public boolean getPreferredState() {
		return preferredState;
	}

	@Override
	public Rectangle getBoundsAbs() {
		Rectangle bounds = MinecraftClientUtil.getWindowBounds();
		return new Rectangle(0, 0, bounds.width(), bounds.height());
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>This implementation provides default caret position for
	 * non-standard input context, such as {@code SignEditScreen}.
	 */
	@Override
	public Point getCaretPos() {
		Rectangle bounds = MinecraftClientUtil.getWindowBounds();
		return new Point(bounds.width() / 3, bounds.height() / 2);
	}
	
	public void setGuiScaleFactor(double factor) {
		this.guiScaleFactor = factor;
		IMManager.updateCompositionWindowPos();
		IMManager.updateCompositionFontSize();
	}
	
	@Override
	public double getGuiScale() {
		return guiScaleFactor;
	}
}
