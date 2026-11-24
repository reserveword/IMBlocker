package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import androidx.compose.ui.platform.PlatformTextInputMethodRequest;
import io.github.reserveword.imblocker.common.gui.mc.ComposeTextFieldWidget;
import kotlin.coroutines.Continuation;

/**
 * Drives IMBlocker's virtual Compose text field from PolyCompose's text-input
 * session. This replaces the old {@code TextInput.start/stop} hook: the session
 * carries both the precise layout request and a reliable focus signal
 * ({@link #inputActive}), so focus and geometry come from a single source.
 *
 * <p>Gated together with the other PolyCompose mixins and uses
 * {@code require = 0} for version safety.
 */
@Mixin(targets = "org.polyfrost.compose.minecraft.screen.SceneContext", remap = false)
public abstract class ComposeTextInputSessionMixin {

	@Shadow
	private boolean inputActive;

	/** Capture the request, which exposes the focused field's live caret rect. */
	@Inject(method = "startInputMethod", at = @At("HEAD"), require = 0, remap = false)
	private void imblocker$captureComposeInputRequest(PlatformTextInputMethodRequest request,
			Continuation<?> continuation, CallbackInfoReturnable<Object> cir) {
		ComposeTextFieldWidget.INSTANCE.setInputRequest(request);
	}

	/**
	 * {@code sync()} is invoked by PolyCompose whenever a session starts or
	 * ends and updates {@code inputActive} before returning. Mirroring it here
	 * gives the exact focus transition, without the old scene-level start/stop
	 * hook. The TAIL injection is skipped when {@code sync()} takes its
	 * no-change early return, which is exactly when no focus update is needed.
	 */
	@Inject(method = "sync", at = @At("TAIL"), require = 0, remap = false)
	private void imblocker$onComposeInputActiveChanged(CallbackInfo ci) {
		if (inputActive) {
			ComposeTextFieldWidget.INSTANCE.focusGained();
		} else {
			ComposeTextFieldWidget.INSTANCE.focusLost();
		}
	}
}
