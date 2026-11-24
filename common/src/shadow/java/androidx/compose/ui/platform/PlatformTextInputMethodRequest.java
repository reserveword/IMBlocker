package androidx.compose.ui.platform;

import androidx.compose.ui.geometry.Rect;
import kotlin.jvm.functions.Function0;

/**
 * Compile-time stub for the Compose text-input request (supplied by PolyCompose
 * at runtime). The getters are lazily evaluated by Compose, so reading them
 * while the field is focused yields live layout data.
 */
public interface PlatformTextInputMethodRequest {
	Function0<Rect> getFocusedRectInRoot();
	Function0<Rect> getTextFieldRectInRoot();
}
