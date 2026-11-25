package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import androidx.compose.ui.text.input.TextInputSession;
import io.github.reserveword.imblocker.common.accessor.ComposeTextInputSessionAccessor;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import io.github.reserveword.imblocker.common.gui.mc.MinecraftFocusableWidget;

@Pseudo
@Mixin(targets = "androidx.compose.foundation.text.LegacyTextFieldState", remap = false)
public abstract class ComposeLegacyTextFieldStateMixin implements MinecraftFocusableWidget {
	@Shadow
	private TextInputSession inputSession;
	
	@Inject(method = "setInputSession", at = @At("TAIL"))
	public void onFocusChanged(CallbackInfo ci) {
		imblocker$onFocusChanged(inputSession != null);
	}
	
	@Inject(method = "getInputSession", at = @At("HEAD"))
	public void syncFocusState(CallbackInfoReturnable<TextInputSession> cir) {
		// Compensate for the retainsScene semantics of PolyCompose.
		if(!isTrulyFocused() && inputSession != null) {
			imblocker$onFocusGained();
		}
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		return getFocusContainer().getBoundsAbs();
	}
	
	@Override
	public double getInternalGuiScale() {
		return 1.0;
	}
	
	@Override
	public Point getCaretPos() {
		return ((ComposeTextInputSessionAccessor) inputSession).imblocker$getCaretPos();
	}
	
	@Override
	public int getFontHeight() {
		return ((ComposeTextInputSessionAccessor) inputSession).imblocker$getFontHeight();
	}
}
