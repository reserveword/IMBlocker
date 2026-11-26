package androidx.compose.ui.text;

import androidx.compose.ui.geometry.Rect;

public abstract class TextLayoutResult {
	public abstract Rect getCursorRect(int offset);
}
