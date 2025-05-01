package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;

@Pseudo
@Mixin(targets = "meteordevelopment.meteorclient.gui.widgets.input.WTextBox", remap = false)
public abstract class MeteorTextFieldMixin extends MeteorWidgetMixin {
	
	@Override
	public boolean isWidgetEditable() {
		return true;
	}
    
	@Inject(method = "setFocused", at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onFocusChanged(isFocused);
	}
	
	@Inject(method = "move", at = @At("TAIL"))
	public void handlePosChanged(double x, double y, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public void onLayoutWidget(CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
}
