package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IdentityHashSet;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

public enum FocusContainer implements FocusableObject {
	MINECRAFT(true, 1.0), IMGUI(false, 4.0);
	
	private double guiScaleFactor;
	
	private boolean isFocused;
	private boolean preferredState = false;
	private FocusableWidget focusedWidget;
	
	private final IdentityHashSet<FocusableWidget> focusCandidates = new IdentityHashSet<>();
	
	private final Runnable locateRealFocusTask = () -> {
		if(focusCandidates.size() > 1) {
			IMBlockerCore.isTrackingFocus = true;
			try {
				MinecraftClientAccessor.INSTANCE.sendSafeCharForFocusTracking(0);
			} catch (Throwable e) {
				IMBlockerCore.LOGGER.warn("failed to locate focus with char simulation");
			}
			if(!IMBlockerCore.isFocusLocated) {
				clearFocus();
			}
			IMBlockerCore.isTrackingFocus = false;
			IMBlockerCore.isFocusLocated = false;
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
			focusCandidates.add(toFocus);
			System.out.println(focusCandidates);
			if(focusCandidates.size() == 1) {
				switchFocus(toFocus);
			}else {
				locateRealFocus();
			}
		}
	}
	
	public void locateRealFocus() {
		IMBlockerCore.invokeLater(locateRealFocusTask);
	}
	
	public void switchFocus(FocusableWidget toFocus) {
		if(IMBlockerCore.isTrackingFocus) {
			IMBlockerCore.isFocusLocated = true;
			if(focusedWidget == toFocus) return;
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
		assert focusCandidates.contains(focusedWidget); //Let's see who will break this.
	}
	
	public void removeFocus(FocusableWidget toRemove) {
		focusCandidates.remove(toRemove);
		System.out.println(focusCandidates);
		if(focusedWidget == toRemove) {
			toRemove.lostFocus();
			focusedWidget = null;
			if(focusCandidates.isEmpty()) {
				if(isFocused) {
					FocusableObject.super.deliverFocus();
				}
			}else if(focusCandidates.size() == 1) {
				switchFocus(focusCandidates.iterator().next());
			}else {
				locateRealFocus();
			}
		}
	}
	
	public void clearFocus() {
		focusCandidates.clear();
		System.out.println(focusCandidates);
		if(focusedWidget != null) {
			focusedWidget.lostFocus();
			focusedWidget = null;
			if(isFocused) {
				FocusableObject.super.deliverFocus();
			}
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
