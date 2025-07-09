package io.github.reserveword.imblocker.common.gui;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.MathHelper;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;

final class MinecraftFocusContext extends FocusContainer {
	
	/**
	 * While there can be only one {@code focusedWidget} at most, multiple
	 * {@code focusCandidate}s may be presented. A {@code focusCandidate} is a
	 * {@link FocusableWidget} that <i>may</i> be interested in keyboard inputs,
	 * whenever the content of {@code focusCandidates} changed, the 
	 * {@code focusedWidget} needs to be relocated through availability comparator
	 * or char simulation. <b>This feature is not required in standard applications,
	 * and designed only to resolve ambiguous focus states in Minecraft context.</b>
	 */
	private final Map<FocusableWidget, Long> focusCandidates = new IdentityHashMap<>();
	
	private final Runnable locateFocusByCharSimulation = () -> {
		if(!focusCandidates.isEmpty()) {
			FocusManager.isTrackingFocus = true;
			try {
				MinecraftClientAccessor.INSTANCE.sendSafeCharForFocusTracking(0);
			} catch (Throwable e) {
				IMBlockerCore.LOGGER.warn("[IMBlocker] Failed to locate focus with char simulation");
			}
			if(!FocusManager.isFocusLocated) {
				restoreContainerFocus();
			}
			IMBlockerCore.LOGGER.info("[IMBlocker] Focus track result: {}", focusedWidget);
			FocusManager.isTrackingFocus = false;
			FocusManager.isFocusLocated = false;
		}
	};
	
	MinecraftFocusContext() {
		super(true);
	}
	
	@Override
	public void requestFocus(FocusableWidget toFocus) {
		focusCandidates.put(toFocus, System.nanoTime());
		IMBlockerCore.LOGGER.info(focusCandidates);
		locateRealFocus();
	}
	
	/**
	 * Locate the <b>real</b> ultimate destination of keyboard inputs through
	 * one of these approaches:
	 * 
	 * <h3>Availability Comparator</h3>
	 * <p>Collect all {@code focusCandidates} that are {@link FocusableWidget#isRenderable() renderable}
	 * and promote the one that requested the focus most recently.
	 * <p>This approach is <b><i>not</i></b> 100% reliable.
	 * 
	 * <h3>Char Simulation</h3>
	 * <p>Send a {@code NUL} char to the game window using internal Minecraft
	 * keyboard handler (equivalent to physical input) that will eventually
	 * passed to the {@code charTyped} method of the <b>real</b> focus owner,
	 * which is injected a callback to pass this result to the container.
	 * <p>This approach <b><i>can</i></b> 100% accurately locate the real focus owner.
	 * Generally, the probe char will be safely absorbed by the injection, but
	 * may leak to the screen if there's no focus owner located and the screen
	 * doesn't filter it.
	 */
	@Override
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
	
	@Override
	public void removeFocus(FocusableWidget toRemove) {
		if(focusCandidates.containsKey(toRemove)) {
			focusCandidates.remove(toRemove);
			IMBlockerCore.LOGGER.info(focusCandidates);
			if (focusCandidates.isEmpty()) {
				restoreContainerFocus();
			} else {
				locateRealFocus();
			} 
		}
	}
	
	@Override
	public void switchFocus(FocusableWidget toFocus) {
		if(FocusManager.isTrackingFocus) {
			FocusManager.isFocusLocated = true;
		}
		super.switchFocus(toFocus);
		verifyFocus();
	}
	
	@Override
	public void clearFocus() {
		focusCandidates.clear();
		IMBlockerCore.LOGGER.info(focusCandidates);
		restoreContainerFocus();
	}
	
	/**
	 * Called from every game event loop to check the <i>real</i> visibility
	 * of all focus candidates.
	 * 
	 * @see FocusableWidget#isRenderable
	 */
	@Override
	public void checkFocusCandidatesVisibility(long lastGameRenderTime) {
		focusCandidates.keySet().forEach(focusCandidate -> {
			if(focusCandidate instanceof MinecraftTextFieldWidget) {
				((MinecraftTextFieldWidget) focusCandidate).checkVisibility(lastGameRenderTime);
			}
		});
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		int x = 0, y = 0;
		Dimension contentSize = MinecraftClientAccessor.INSTANCE.getContentSize();
		if(IMBlockerCore.hasMod("axiom")) {
			AxiomGuiMonitor axiomMonitor = AxiomGuiMonitor.getInstance();
			if(axiomMonitor != null && axiomMonitor.isAxiomEditorShowing()) {
				x = axiomMonitor.getGameContentOffsetX();
				y = axiomMonitor.getGameContentOffsetY();
			}
		}
		return new Rectangle(x, y, contentSize.width(), contentSize.height());
	}
	
	@Override
	public Point getCaretPos() {
		Dimension contentSize = MinecraftClientAccessor.INSTANCE.getContentSize();
		return new Point(contentSize.width() / 3, contentSize.height() / 2);
	}
}
