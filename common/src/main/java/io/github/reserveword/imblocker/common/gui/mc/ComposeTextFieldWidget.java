package io.github.reserveword.imblocker.common.gui.mc;

import androidx.compose.ui.geometry.Rect;
import androidx.compose.ui.platform.PlatformTextInputMethodRequest;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.MinecraftClientUtil;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import kotlin.jvm.functions.Function0;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Set;

/**
 * Virtual focus owner standing for the text field currently focused inside a
 * Compose scene. A Compose {@code BasicTextField} is not a Minecraft
 * {@code EditBox}, so the vanilla widget mixins cannot observe it; the
 * Compose-core session hooks ({@code ComposeTextInputServiceMixin} for the
 * lifecycle, {@code ComposeSessionRequestMixin} for the request) drive this
 * singleton instead. The hooks are host-agnostic, so this works for PolyCompose
 * screens and HUD scenes alike, and for any other mod hosting Compose scenes
 * on its own {@code PlatformContext}.
 *
 * <p>Precise layout data comes from the {@link PlatformTextInputMethodRequest}
 * delivered to the session. Its lazy getters expose the focused field's
 * rectangle and caret rectangle in scene-root coordinates, which match
 * IMBlocker's content-area point space, so the IME candidate window can follow
 * the real caret instead of falling back to the content centre.
 *
 * <p>Sessions are tracked per scene (each scene owns one
 * {@code TextInputService}) and reference-counted: Compose cancels sessions
 * asynchronously, so a stale session teardown from a closing scene can land
 * after a fresh scene's session started, and a global focus toggle would then
 * kill the new focus permanently (Compose never re-announces a still-alive
 * session). Counting per service keeps the widget focused as long as any scene
 * still holds a session. Focus that the environment clears while a session is
 * alive is re-asserted on every tick (see {@link #ensureTickRegistered}),
 * covering restored-focus flows where no new session event ever fires.
 *
 * <p>Tested against Compose Multiplatform 1.13.0-alpha02 (PolyCompose 3.0.7).
 */
public final class ComposeTextFieldWidget implements MinecraftFocusableWidget {

	public static final ComposeTextFieldWidget INSTANCE = new ComposeTextFieldWidget();

	private PlatformTextInputMethodRequest inputRequest;
	/** Live sessions keyed by their owning per-scene {@code TextInputService}. */
	private final Set<Object> liveSessions = Collections.newSetFromMap(new IdentityHashMap<>());
	private static boolean tickRegistered;
	private static boolean missingRectWarned;
	private long lastDebugLog;
	private int missingRectTicks;

	private ComposeTextFieldWidget() {}

	// Compose has no "cursor moved" callback we can hook without touching
	// unstable internals. Poll while this widget owns focus so the candidate
	// window keeps following the caret as the user types or moves it.
	private static void ensureTickRegistered() {
		if (!tickRegistered) {
			tickRegistered = true;
			IMBlockerCore.registerClientTickEvent(() -> {
				ComposeTextFieldWidget widget = INSTANCE;
				if (widget.isTrulyFocused()) {
					IMManager.updateCaretPosition();
					widget.checkRectData();
					widget.debugLayout();
				} else if (widget.hasLiveSession() && MinecraftScreenMonitor.isCurrentScreenCompose()) {
					// A session is alive and a Compose screen is on top, but the
					// focus container dropped our focus: the field restored its
					// previous focus state while the screen opened, a stale
					// teardown raced the fresh session, or the screen was simply
					// re-shown. Compose has no callback for any of these (a
					// still-alive session is never re-announced), so re-assert
					// the focus that the old PolyCompose sync() hook used to
					// replay every frame.
					widget.imblocker$onFocusGained();
				}
			});
		}
	}

	/** A Compose text-input session began on the given per-scene text-input service. */
	public synchronized void sessionStarted(Object service) {
		if (liveSessions.add(service)) {
			ensureTickRegistered();
			imblocker$onFocusGained();
		}
	}

	/**
	 * The session running on the given per-scene service was cancelled. Other
	 * scenes may still hold sessions, so the widget only loses focus when the
	 * last one ends.
	 */
	public synchronized void sessionEnded(Object service) {
		if (liveSessions.remove(service) && liveSessions.isEmpty()) {
			inputRequest = null;
			imblocker$onFocusLost();
		}
	}

	/** Whether any scene currently holds a live Compose text-input session. */
	public synchronized boolean hasLiveSession() {
		return !liveSessions.isEmpty();
	}

	/** Attach the request delivered for the newest session (live rect source). */
	public synchronized void setInputRequest(PlatformTextInputMethodRequest request) {
		ensureTickRegistered();
		inputRequest = request;
	}

	private Rect getFieldRect() {
		PlatformTextInputMethodRequest request = inputRequest;
		if (request == null) return null;
		try {
			Function0<Rect> getter = request.getTextFieldRectInRoot();
			return getter == null ? null : getter.invoke();
		} catch (Throwable t) {
			return null;
		}
	}

	private Rect getFocusedRect() {
		PlatformTextInputMethodRequest request = inputRequest;
		if (request == null) return null;
		try {
			Function0<Rect> getter = request.getFocusedRectInRoot();
			return getter == null ? null : getter.invoke();
		} catch (Throwable t) {
			return null;
		}
	}

	@Override
	public Rectangle getBoundsAbs() {
		Rect field = getFieldRect();
		if (field != null && field.getWidth() > 0f && field.getHeight() > 0f) {
			return new Rectangle((int) field.getLeft(), (int) field.getTop(),
					(int) field.getWidth(), (int) field.getHeight());
		}
		var size = MinecraftClientUtil.getContentSize();
		return new Rectangle(0, 0, size.width(), size.height());
	}

	@Override
	public Point getCaretPos() {
		Rect field = getFieldRect();
		Rect caret = getFocusedRect();
		if (field != null && caret != null && field.getWidth() > 0f && field.getHeight() > 0f) {
			int x = (int) Math.round(caret.getLeft() - field.getLeft());
			int y = (int) Math.round(caret.getTop() - field.getTop() + caret.getHeight() / 2f);
			return new Point(x, y);
		}
		var size = MinecraftClientUtil.getContentSize();
		return new Point(size.width() / 3, size.height() / 2);
	}

	// Use the framework default. Deriving this from the caret rect inflated the
	// value far beyond the vanilla text field's 8 UI pixels, which pushed the
	// candidate window noticeably downward.
	@Override
	public int getFontHeight() {
		return 8;
	}

	// The layout getters above are best-effort (catch Throwable), so if Compose
	// changes its request API the widget silently degrades to the centre
	// fallback. Raise a one-shot warning once the lack of rect data persists,
	// so that degradation is diagnosable instead of invisible.
	private void checkRectData() {
		if (inputRequest == null) {
			// The lifecycle hook fired but no request was captured, i.e. the
			// request capture mixin is not applying.
			if (++missingRectTicks >= 20 && !missingRectWarned) { // ~1s without any layout data
				missingRectWarned = true;
				IMBlockerCore.LOGGER.warn("[IMBlocker] Compose session request capture inactive; "
						+ "candidate window falls back to screen centre");
			}
			return;
		}
		if (getFieldRect() != null || getFocusedRect() != null) {
			missingRectTicks = 0;
			return;
		}
		if (++missingRectTicks >= 20 && !missingRectWarned) { // ~1s without any layout data
			missingRectWarned = true;
			IMBlockerCore.LOGGER.warn("[IMBlocker] Compose returned no layout rect data; "
					+ "candidate window falls back to screen centre "
					+ "(tested with Compose Multiplatform 1.13.0-alpha02 / PolyCompose 3.0.7)");
		}
	}

	private void debugLayout() {
		if (!Boolean.getBoolean("imblocker.debug.compose")) return;
		long now = System.nanoTime();
		if (now - lastDebugLog < 1_000_000_000L) return;
		lastDebugLog = now;
		Rect field = getFieldRect();
		Rect caret = getFocusedRect();
		var bounds = getBoundsAbs();
		Point pos = getCaretPos();
		IMBlockerCore.LOGGER.info("[IMBlocker] compose layout: field={} caret={} -> bounds={} caretPos={} guiScale={} density={}",
				fmt(field), fmt(caret), bounds, pos, getGuiScale(), io.github.reserveword.imblocker.common.InputSystem.getWindowPixelDensity());
	}

	private static String fmt(Rect r) {
		if (r == null) return "null";
		return String.format(Locale.ROOT, "(%.1f,%.1f %.1fx%.1f)",
				r.getLeft(), r.getTop(), r.getWidth(), r.getHeight());
	}
}
