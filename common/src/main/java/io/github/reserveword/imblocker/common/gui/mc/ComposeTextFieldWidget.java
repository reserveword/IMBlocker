package io.github.reserveword.imblocker.common.gui.mc;

import androidx.compose.ui.geometry.Rect;
import androidx.compose.ui.platform.PlatformTextInputMethodRequest;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.MinecraftClientUtil;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import kotlin.jvm.functions.Function0;

/**
 * Virtual focus owner standing for the text field currently focused inside a
 * Compose scene (PolyCompose). A Compose {@code BasicTextField} is not a
 * Minecraft {@code EditBox}, so the vanilla widget mixins cannot observe it;
 * PolyCompose's text-input session callbacks drive this singleton instead.
 *
 * <p>Precise layout data comes from the {@link PlatformTextInputMethodRequest}
 * delivered to {@code SceneContext.startInputMethod}. Its lazy getters expose
 * the focused field's rectangle and caret rectangle in scene-root coordinates,
 * which match IMBlocker's content-area point space, so the IME candidate window
 * can follow the real caret instead of falling back to the content centre.
 */
public final class ComposeTextFieldWidget implements MinecraftFocusableWidget {

	public static final ComposeTextFieldWidget INSTANCE = new ComposeTextFieldWidget();

	private PlatformTextInputMethodRequest inputRequest;
	private static boolean tickRegistered;
	private long lastDebugLog;

	private ComposeTextFieldWidget() {}

	// Compose has no "cursor moved" callback we can hook without touching
	// unstable internals. Poll while this widget owns focus so the candidate
	// window keeps following the caret as the user types or moves it.
	private static void ensureTickRegistered() {
		if (!tickRegistered) {
			tickRegistered = true;
			IMBlockerCore.registerClientTickEvent(() -> {
				ComposeTextFieldWidget widget = INSTANCE;
				if (widget.inputRequest != null && widget.isTrulyFocused()) {
					IMManager.updateCaretPosition();
					widget.debugLayout();
				}
			});
		}
	}

	public void setInputRequest(PlatformTextInputMethodRequest request) {
		ensureTickRegistered();
		inputRequest = request;
	}

	public void clearInputRequest() {
		inputRequest = null;
	}

	public void focusGained() {
		imblocker$onFocusGained();
	}

	public void focusLost() {
		clearInputRequest();
		imblocker$onFocusLost();
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
		return String.format(java.util.Locale.ROOT, "(%.1f,%.1f %.1fx%.1f)",
				r.getLeft(), r.getTop(), r.getWidth(), r.getHeight());
	}
}
