package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import androidx.compose.ui.platform.PlatformTextInputMethodRequest;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.gui.mc.ComposeTextFieldWidget;
import kotlin.coroutines.Continuation;

/**
 * Drives IMBlocker's virtual Compose text field from PolyCompose's text-input
 * session. This replaces the old {@code TextInput.start/stop} hook: the session
 * carries both the precise layout request and a reliable focus signal
 * ({@link #inputActive}), so focus and geometry come from a single source.
 *
 * <p>Uses a string (soft) target with {@code require = 0}, so the mixin is
 * applied lazily only when the PolyCompose {@code SceneContext} class actually
 * loads - no mod-id gating is needed, which covers every PolyCompose consumer
 * (standalone mod, nested jar, shaded bundle alike). The failure modes under
 * PolyCompose API drift are asymmetric: a renamed {@link #inputActive} field
 * fails the mixin application loudly, whereas a changed {@code sync} or
 * {@code startInputMethod} signature silently disables the injection. The
 * one-shot log in the capture handler lets users verify the hook is live.
 *
 * <p>Tested against PolyCompose 3.0.7.
 */
@Mixin(targets = "org.polyfrost.compose.minecraft.screen.SceneContext", remap = false)
public abstract class ComposeTextInputSessionMixin {

	@Shadow
	private boolean inputActive;

	private static boolean captureLogged;

	/** Capture the request, which exposes the focused field's live caret rect. */
	@Inject(method = "startInputMethod", at = @At("HEAD"), require = 0, remap = false)
	private void imblocker$captureComposeInputRequest(PlatformTextInputMethodRequest request,
			Continuation<?> continuation, CallbackInfoReturnable<Object> cir) {
		// One-shot proof that this injection applied and fired; if the PolyCompose
		// method signature ever drifts, this log stays absent, which is the signal
		// that the session hook is dead.
		if (!captureLogged) {
			captureLogged = true;
			IMBlockerCore.LOGGER.info("[IMBlocker] Compose text-input session hook active (tested with PolyCompose 3.0.7)");
		}
		ComposeTextFieldWidget.INSTANCE.setInputRequest((Object) this, request);
	}

	/**
	 * {@code sync()} is invoked by PolyCompose whenever a session starts or
	 * ends and updates {@code inputActive} before returning. Mirroring it here
	 * gives the exact focus transition, without the old scene-level start/stop
	 * hook. The TAIL injection is skipped when {@code sync()} takes its
	 * no-change early return, which is exactly when no focus update is needed.
	 *
	 * <p>The scene instance is passed as the session identity: a session can
	 * outlive IMBlocker's focus (screen hidden/covered while the field stays
	 * focused) and re-fire {@code sync(true)} without a new
	 * {@code startInputMethod}, so the widget must re-attach this scene's own
	 * captured request instead of relying on a single global one.
	 */
	@Inject(method = "sync", at = @At("TAIL"), require = 0, remap = false)
	private void imblocker$onComposeInputActiveChanged(CallbackInfo ci) {
		if (inputActive) {
			ComposeTextFieldWidget.INSTANCE.focusGained((Object) this);
		} else {
			ComposeTextFieldWidget.INSTANCE.focusLost((Object) this);
		}
	}

	/** Release the captured request when the scene context is disposed. */
	@Inject(method = "close", at = @At("HEAD"), require = 0, remap = false)
	private void imblocker$onComposeSceneClosed(CallbackInfo ci) {
		ComposeTextFieldWidget.INSTANCE.sessionClosed((Object) this);
	}
}
