package io.github.reserveword.imblocker.common.gui;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.MathHelper;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

/**
 * <p>This class represents the focus destination of the game window. The game
 * window must have a {@code FocusContainer} to deliver the focus, on receiving
 * the focus, this container will transfer it to its focus destination -
 * {@code focusedWidget} or directly hold it if the {@code focusedWidget} is
 * null.
 * 
 * <p>While there can be only one {@code focusedWidget} at most, multiple
 * {@code focusCandidate}s may be presented. A {@code focusCandidate} is a
 * {@link FocusableWidget} that <i>may</i> be interested in keyboard inputs,
 * whenever the content of {@code focusCandidates} changed, the 
 * {@code focusedWidget} needs to be relocated through availability comparator
 * or char simulation. <b>This feature is not required in standard applications,
 * and designed only to resolve ambiguous focus states in Minecraft context.</b>
 * 
 * <p>The corresponding Minecraft GUI context of a {@code FocusContainer} is all
 * non-textfield widgets, as they share the same IME preferences.
 * 
 * <p>This class is a base part of <b>IMBlocker's focus management system</b>.
 * 
 * @see FocusManager
 * @see FocusableObject
 * @see FocusableWidget
 * @author LitnhJacuzzi
 * @since 5.1.0
 */
public enum FocusContainer implements FocusableObject {
	MINECRAFT(true, 1.0), IMGUI(false, 4.0);
	
	private double guiScaleFactor;
	
	private boolean isFocused;
	private boolean preferredState = false;
	
	private FocusableWidget focusedWidget;
	private final Map<FocusableWidget, Long> focusCandidates = new IdentityHashMap<>();
	
	private final Runnable locateFocusByCharSimulation = () -> {
		if(!focusCandidates.isEmpty()) {
			FocusManager.isTrackingFocus = true;
			try {
				MinecraftClientAccessor.INSTANCE.sendSafeCharForFocusTracking(0);
			} catch (Throwable e) {
				IMBlockerCore.LOGGER.warn("failed to locate focus with char simulation");
			}
			if(!FocusManager.isFocusLocated) {
				restoreContainerFocus();
			}
			System.out.println("Focus tracking result: " + focusedWidget);
			FocusManager.isTrackingFocus = false;
			FocusManager.isFocusLocated = false;
		}
	};
	
	private FocusContainer(boolean defaultFocusState, double guiScale) {
		isFocused = defaultFocusState;
		guiScaleFactor = guiScale;
	}
	
	public void requestFocus(FocusableWidget toFocus) {
		focusCandidates.put(toFocus, System.nanoTime());
		System.out.println(focusCandidates);
		locateRealFocus();
	}
	
	public void locateRealFocus() {
		if(IMBlockerConfig.INSTANCE.isTwoFactorFocusTrackingEnabled()) {
			IMBlockerCore.invokeLater(locateFocusByCharSimulation);
		}else {
			Optional<FocusableWidget> promotedFocusCandidate = focusCandidates.keySet().stream()
					.filter(FocusableWidget::isRenderable)
					.max((o1, o2) -> MathHelper.clampLong(focusCandidates.get(o1) - focusCandidates.get(o2)));
			if(promotedFocusCandidate.isPresent()) {
				switchFocus(promotedFocusCandidate.get());
			}else {
				restoreContainerFocus();
			}
		}
	}
	
	private void verifyFocus() {
		assert focusCandidates.containsKey(focusedWidget); //Let's see who will break this.
	}
	
	public void switchFocus(FocusableWidget toFocus) {
		if(FocusManager.isTrackingFocus) {
			FocusManager.isFocusLocated = true;
		}
		
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
		verifyFocus();
	}
	
	public void removeFocus(FocusableWidget toRemove) {
		if(focusCandidates.containsKey(toRemove)) {
			focusCandidates.remove(toRemove);
			System.out.println(focusCandidates);
			if (focusCandidates.isEmpty()) {
				restoreContainerFocus();
			} else {
				locateRealFocus();
			} 
		}
	}
	
	private void restoreContainerFocus() {
		if(focusedWidget != null) {
			if(isFocused) {
				focusedWidget.lostFocus();
				FocusableObject.super.deliverFocus();
			}
			focusedWidget = null;
		}
	}
	
	public void clearFocus() {
		focusCandidates.clear();
		System.out.println(focusCandidates);
		restoreContainerFocus();
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
		}
	}
	
	public void checkFocusCandidatesVisibility(long lastGameRenderTime) {
		focusCandidates.keySet().forEach(focusCandidate -> {
			if(focusCandidate instanceof MinecraftTextFieldWidget) {
				((MinecraftTextFieldWidget) focusCandidate).checkVisibility(lastGameRenderTime);
			}
		});
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
		Rectangle bounds = MinecraftClientAccessor.INSTANCE.getWindowBounds();
		return new Rectangle(0, 0, bounds.width(), bounds.height());
	}
	
	@Override
	public Point getCaretPos() {
		Rectangle bounds = MinecraftClientAccessor.INSTANCE.getWindowBounds();
		return new Point(bounds.width() / 3, bounds.height() / 2);
	}
	
	public void setGuiScaleFactor(double factor) {
		this.guiScaleFactor = factor;
	}
	
	@Override
	public double getGuiScale() {
		return guiScaleFactor;
	}
}
