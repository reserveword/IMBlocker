package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.gui.mc.ComposeTextFieldWidget;

/**
 * Hooks PolyCompose's text-input session so IMBlocker can track focus of a
 * Compose {@code BasicTextField} (for example OneConfig's search bar).
 *
 * <p>{@code TextInput.start/stop} are {@code @JvmStatic} methods on a Kotlin
 * object, so they appear as static methods
 * {@code org.polyfrost.compose.minecraft.platform.TextInput.start(Object)} and
 * {@code .stop(Object)}. They fire exactly when a Compose field takes/gives up
 * text input. They are only wired on 26.1+ (SDL) builds of PolyCompose.
 */
@Mixin(targets = "org.polyfrost.compose.minecraft.platform.TextInput", remap = false)
public abstract class PolyComposeTextInputMixin {

	@Inject(method = "start", at = @At("HEAD"), require = 0, remap = false)
	private static void imblocker$onComposeInputStart(Object owner, CallbackInfo ci) {
		if (Boolean.getBoolean("imblocker.debug.compose"))
			IMBlockerCore.LOGGER.info("[IMBlocker] PolyCompose text input started: {}", owner);
		ComposeTextFieldWidget.INSTANCE.focusGained();
	}

	@Inject(method = "stop", at = @At("HEAD"), require = 0, remap = false)
	private static void imblocker$onComposeInputStop(Object owner, CallbackInfo ci) {
		if (Boolean.getBoolean("imblocker.debug.compose"))
			IMBlockerCore.LOGGER.info("[IMBlocker] PolyCompose text input stopped: {}", owner);
		ComposeTextFieldWidget.INSTANCE.focusLost();
	}
}
