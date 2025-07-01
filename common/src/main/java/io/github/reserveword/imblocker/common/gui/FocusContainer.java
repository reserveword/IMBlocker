package io.github.reserveword.imblocker.common.gui;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.MathHelper;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

public enum FocusContainer implements FocusableObject {
	MINECRAFT(true, 1.0), IMGUI(false, 4.0);
	
	private double guiScaleFactor;
	
	private boolean isFocused;
	private boolean preferredState = false;
	private FocusableWidget focusedWidget;
	
	private final Map<FocusableWidget, Long> focusCandidates = new IdentityHashMap<>();
	
	private final Runnable locateRealFocusTask = () -> {
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
	
	public FocusableWidget getFocusedWidget() {
		return focusedWidget;
	}
	
	public void requestFocus(FocusableWidget toFocus) {
		if(focusedWidget != toFocus) {
			focusCandidates.put(toFocus, System.nanoTime());
			System.out.println(focusCandidates);
			locateRealFocus();
		}
	}
	
	public void locateRealFocus() {
		if(IMBlockerConfig.INSTANCE.isTwoFactorFocusTrackingEnabled()) {
			IMBlockerCore.invokeLater(locateRealFocusTask);
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
			if(focusedWidget == toFocus) {
				verifyFocus();
				return;
			}
		}
		
		if(isFocused) {
			if(focusedWidget != null) {
				focusedWidget.lostFocus();
			}
			focusedWidget = toFocus;
			focusedWidget.deliverFocus();
		}else {
			focusedWidget = toFocus;
		}
		verifyFocus();
	}
	
	public void removeFocus(FocusableWidget toRemove) {
		focusCandidates.remove(toRemove);
		System.out.println(focusCandidates);
		if(focusedWidget == toRemove) {
			if(focusCandidates.isEmpty()) {
				restoreContainerFocus();
			}else {
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
	
	public Set<FocusableWidget> getFocusCandidates() {
		return focusCandidates.keySet();
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
