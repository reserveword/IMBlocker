package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import gg.essential.gui.common.input.AbstractTextInput.LinePosition;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.CursorInfo;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftAbstractTextInputWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import kotlin.Pair;

@Mixin(targets = "gg.essential.gui.common.input.AbstractTextInput", remap = false)
public abstract class EssentialAbstractTextInputMixin extends EssentialUIComponentMixin implements MinecraftAbstractTextInputWidget<CursorInfo> {
	
	@Shadow
	private LinePosition cursor;
	
	@Shadow
	private boolean active;
	
	@Shadow
	private float lineHeight;

	@Override
	public void onFocusGained(CallbackInfo ci) {
		imblocker$onFocusGained();
	}
	
	@Override
	public void onFocusLost(CallbackInfo ci) {
		imblocker$onFocusLost();
	}
	
	@Override
	public void checkFocusTracking(char c, int keyCode, CallbackInfo ci) {
		if(FocusManager.isTrackingFocus) {
			if(active) {
				FocusContainer.MINECRAFT.switchFocus(this);
			}
			ci.cancel();
		}
	}
	
	@Inject(method = "setCursor", at = @At("TAIL"))
	public void onCursorChanged(LinePosition value, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Inject(method = "setHorizontalScrollingOffset", at = @At("TAIL"))
	public void onHorizontalScrolling(float f, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Inject(method = "setTargetVerticalScrollingOffset", at = @At("TAIL"))
	public void onVerticalScrolling(float f, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public Point getCaretPos() {
		Pair<Float, Float> caretPos = cursor.toScreenPos();
		return new Point(getGuiScale(), caretPos.getFirst().intValue(), caretPos.getSecond().intValue());
	}
	
	@Override
	public int getFontHeight() {
		return (int) (lineHeight * getTextScale());
	}
}
