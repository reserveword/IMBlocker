package io.github.reserveword.imblocker.common.gui.mc;

import androidx.compose.ui.geometry.Rect;
import androidx.compose.ui.platform.PlatformTextInputMethodRequest;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.MinecraftClientUtil;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import kotlin.jvm.functions.Function0;

import java.util.Locale;

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
 * <p>Compose keeps exactly one session alive at a time ({@code SessionMutex}
 * cancels the previous one before starting the next), so a single request slot
 * is sufficient: the newest captured request always belongs to the active
 * session. A session can outlive IMBlocker's attention (screen hidden/covered
 * while the field stays focused) without any callback - the widget simply
 * stays focused, which mirrors Compose's own state.
 *
 * <p>Tested against Compose Multiplatform 1.13.0-alpha02 (PolyCompose 3.0.7).
 */
public final class ComposeTextFieldWidget implements MinecraftFocusableWidget {

	public static final ComposeTextFieldWidget INSTANCE = new ComposeTextFieldWidget();

	private PlatformTextInputMethodRequest inputRequest;
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
				// Poll whenever this widget truly owns focus. Do not gate on
				// inputRequest: if the request capture missed (Compose drift)
				// while the lifecycle hook still fired, gating would silence
				// debugLayout() and checkRectData() as well, making the centre
				// fallback undiagnosable.
				if (widget.isTrulyFocused()) {
					IMManager.updateCaretPosition();
					widget.checkRectData();
					widget.debugLayout();
				}
			});
		}
	}

	/** A Compose text-input session began: a text field now owns the IME focus. */
	public void sessionStarted() {
		ensureTickRegistered();
		imblocker$onFocusGained();
	}

	/**
	 * The active session's coroutine was cancelled: focus left the field (or
	 * moved to another one, whose start callback then re-focuses this widget).
	 */
	public void sessionEnded() {
		inputRequest = null;
		imblocker$onFocusLost();
	}

	/** Attach the request delivered for the newest session (live rect source). */
	public void setInputRequest(PlatformTextInputMethodRequest request) {
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
