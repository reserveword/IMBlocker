package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import androidx.compose.foundation.text.LegacyTextFieldState;
import androidx.compose.ui.text.input.OffsetMapping;
import androidx.compose.ui.text.input.TextFieldValue;
import io.github.reserveword.imblocker.common.accessor.ComposeLegacyTextFieldStateAdapter;

@Pseudo
@SuppressWarnings("deprecation")
@Mixin(targets = "androidx.compose.foundation.text.CoreTextFieldKt", remap = false)
public abstract class ComposeCoreTextFieldKtMixin {
	@Inject(method = "notifyFocusedRect", at = @At("TAIL"))
	private static void updateCaretPos(LegacyTextFieldState state, 
			TextFieldValue value, OffsetMapping offsetMapping, CallbackInfo ci) {
		((ComposeLegacyTextFieldStateAdapter) state).imblocker$updateCaretPos(value, offsetMapping);
	}
}
