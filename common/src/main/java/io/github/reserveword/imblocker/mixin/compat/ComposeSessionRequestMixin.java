package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import androidx.compose.ui.platform.PlatformTextInputMethodRequest;
import kotlin.coroutines.Continuation;

import io.github.reserveword.imblocker.common.gui.mc.ComposeTextFieldWidget;

/**
 * Captures the {@link PlatformTextInputMethodRequest} of the newest Compose
 * text-input session at its single funnel point inside Compose core, replacing
 * the former capture from PolyCompose's {@code SceneContext} override.
 *
 * <p>On Compose Multiplatform desktop targets the per-scene session machinery
 * lives in the private inner class
 * {@code RootNodeOwner$OwnerImpl$TextInputSession}: its suspend
 * {@code startInputMethod} forwards every session request to the host
 * {@code PlatformContext} (PolyCompose's scene contexts, HUD text contexts, or
 * any other port's implementation). Hooking here instead of any host class
 * means the live field/caret rectangles arrive regardless of which mod hosts
 * the Compose scene, and the request itself is the stock Compose object whose
 * lazy getters ({@code focusedRectInRoot}/{@code textFieldRectInRoot}) expose
 * the focused field's layout - exactly the data the IME candidate window
 * needs.
 *
 * <p>This mixin is optional by design: the session lifecycle (focus gained and
 * lost) is tracked independently by {@link ComposeTextInputServiceMixin}. If
 * the private inner class is ever restructured, this capture silently stops
 * ({@code require = 0}) and only the candidate-window position degrades to the
 * screen-centre fallback, which {@code ComposeTextFieldWidget} reports as a
 * warning.
 *
 * <p>Tested against Compose Multiplatform 1.13.0-alpha02 (PolyCompose 3.0.7).
 */
@Mixin(targets = "androidx.compose.ui.node.RootNodeOwner$OwnerImpl$TextInputSession", remap = false)
public abstract class ComposeSessionRequestMixin {

	@Inject(method = "startInputMethod", at = @At("HEAD"), require = 0, remap = false)
	private void imblocker$captureComposeInputRequest(PlatformTextInputMethodRequest request,
			Continuation<?> continuation, CallbackInfoReturnable<Object> cir) {
		ComposeTextFieldWidget.INSTANCE.setInputRequest(request);
	}
}
