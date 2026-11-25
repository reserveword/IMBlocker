package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.gui.mc.ComposeTextFieldWidget;

/**
 * Tracks the Compose text-input session lifecycle inside Compose core itself,
 * replacing the former PolyCompose {@code SceneContext} hook.
 *
 * <p>On Compose Multiplatform desktop targets every text field session funnel
 * through {@code androidx.compose.ui.text.input.TextInputService}, a public
 * {@code ui-text} class: the per-scene session machinery in
 * {@code RootNodeOwner} starts each session with the internal no-arg
 * {@code startInput()} and its cancellation callback ends it with the no-arg
 * {@code stopInput()}. These two calls are exactly "a Compose text field now
 * owns the IME focus" and "it no longer does", for any host integration -
 * PolyCompose screens, PolyCompose HUD scenes, or any other port that hosts
 * scenes on its own {@code PlatformContext}. {@code SessionMutex} cancels the
 * previous session before a new one starts, so a field-to-field focus switch
 * always arrives as stop-then-start, never start-then-stop.
 *
 * <p>Uses a string (soft) target with {@code require = 0}, so the mixin is
 * applied lazily only when the Compose runtime actually loads - no mod-id
 * gating is needed, which covers every Compose consumer (standalone mod,
 * nested jar, shaded bundle alike). If a Compose build ever renames the
 * internal no-arg overloads, the injections silently stop and IMBlocker
 * simply never sees Compose focus; the one-shot log in
 * {@link #imblocker$onComposeSessionStarted} lets users verify the hook is
 * live.
 *
 * <p>Tested against Compose Multiplatform 1.13.0-alpha02 (PolyCompose 3.0.7).
 */
@Mixin(targets = "androidx.compose.ui.text.input.TextInputService", remap = false)
public abstract class ComposeTextInputServiceMixin {

	private static boolean sessionLogged;

	/** Internal no-arg overload fired when a Compose text-input session begins. */
	@Inject(method = "startInput()V", at = @At("HEAD"), require = 0, remap = false)
	private void imblocker$onComposeSessionStarted(CallbackInfo ci) {
		// One-shot proof that this injection applied and fired; if the internal
		// no-arg overload ever drifts, this log stays absent, which is the
		// signal that the Compose session hook is dead.
		if (!sessionLogged) {
			sessionLogged = true;
			IMBlockerCore.LOGGER.info("[IMBlocker] Compose text-input session hook active "
					+ "(tested with Compose Multiplatform 1.13.0-alpha02 / PolyCompose 3.0.7)");
		}
		ComposeTextFieldWidget.INSTANCE.sessionStarted();
	}

	/** Internal no-arg overload fired when the session coroutine is cancelled. */
	@Inject(method = "stopInput()V", at = @At("HEAD"), require = 0, remap = false)
	private void imblocker$onComposeSessionStopped(CallbackInfo ci) {
		ComposeTextFieldWidget.INSTANCE.sessionEnded();
	}
}
