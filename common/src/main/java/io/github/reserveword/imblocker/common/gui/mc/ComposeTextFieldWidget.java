package io.github.reserveword.imblocker.common.gui.mc;

import io.github.reserveword.imblocker.common.MinecraftClientUtil;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;

/**
 * <p>Virtual focus owner standing for the text field currently focused inside a
 * Compose scene (PolyCompose). A Compose {@code BasicTextField} is not a
 * Minecraft {@code EditBox}, so the vanilla widget mixins cannot observe it;
 * PolyCompose's text-input session callbacks drive this singleton instead.
 *
 * <p>PolyCompose exposes no public API for the focused field's rectangle, so
 * the IME candidate window uses the same content-area fallback the old
 * screen-whitelist approach used. Precise caret tracking would require a
 * PolyCompose-side hook and is left for a follow-up.
 */
public final class ComposeTextFieldWidget implements MinecraftFocusableWidget {

	public static final ComposeTextFieldWidget INSTANCE = new ComposeTextFieldWidget();

	private ComposeTextFieldWidget() {}

	public void focusGained() {
		imblocker$onFocusGained();
	}

	public void focusLost() {
		imblocker$onFocusLost();
	}

	@Override
	public Rectangle getBoundsAbs() {
		var size = MinecraftClientUtil.getContentSize();
		return new Rectangle(0, 0, size.width(), size.height());
	}

	@Override
	public Point getCaretPos() {
		var size = MinecraftClientUtil.getContentSize();
		return new Point(size.width() / 3, size.height() / 2);
	}
}
