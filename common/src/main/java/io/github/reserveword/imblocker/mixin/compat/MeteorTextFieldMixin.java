package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.Point;

@Pseudo
@Mixin(targets = "meteordevelopment.meteorclient.gui.widgets.input.WTextBox", remap = false)
public abstract class MeteorTextFieldMixin extends MeteorWidgetMixin implements MinecraftTextFieldWidget {
	
	@Shadow protected int cursor;
	@Shadow protected double textStart;
	
	@Shadow
	protected boolean focused;
	
	@Shadow
	protected abstract double getTextWidth(int pos);

	@Inject(method = "setFocused", at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		imblocker$onFocusChanged(focused);
	}
	
	@Inject(method = "onCharTyped", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(char c, CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.isTrackingFocus) {
			if(focused) {
				FocusContainer.MINECRAFT.switchFocus(this);
				cir.setReturnValue(true);
			}else {
				cir.setReturnValue(false);
			}
		}
	}
	
	@Inject(method = "cursorChanged", at = @At("TAIL"))
	public void onCursorChanged(CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Inject(method = "move", at = @At("TAIL"))
	public void handlePosChanged(double x, double y, CallbackInfo ci) {
		imblocker$onBoundsChanged();
	}
	
	@Override
	public void onLayoutWidget(CallbackInfo ci) {
		imblocker$onBoundsChanged();
		IMManager.updateCompositionFontSize();
	}
	
	@Override
	public Point getCaretPos() {
		int caretX = (int) (getTextWidth(cursor) - textStart + pad());
		return new Point(caretX, (int) (height / 3.5));
	}
	
	@Override
	public int getFontHeight() {
		return (int) (height * 3/7);
	}
}
