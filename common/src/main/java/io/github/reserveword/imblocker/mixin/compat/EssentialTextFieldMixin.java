package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import gg.essential.gui.common.input.AbstractTextInput.LinePosition;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.MinecraftMultilineEditBoxWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import kotlin.Pair;

@Mixin(targets = "gg.essential.gui.common.input.AbstractTextInput", remap = false)
public abstract class EssentialTextFieldMixin extends EssentialUIComponentMixin implements MinecraftMultilineEditBoxWidget {
	
	@Shadow
	private LinePosition cursor;

	@Override
	public void onFocusGained(CallbackInfo ci) {
		imblocker$onFocusGained();
	}
	
	@Override
	public void onFocusLost(CallbackInfo ci) {
		imblocker$onFocusLost();
	}
	
	@Inject(method = "setCursor", at = @At("TAIL"))
	public void onCursorChanged(LinePosition value, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public Point getCaretPos() {
		Pair<Float, Float> caretPos = cursor.toScreenPos();
		return new Point(getGuiScale(), caretPos.getFirst().intValue(), 2 + caretPos.getSecond().intValue());
	}
}
