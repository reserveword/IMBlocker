package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import androidx.compose.ui.geometry.Rect;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.accessor.ComposeTextInputSessionAccessor;
import io.github.reserveword.imblocker.common.gui.Point;

@Pseudo
@Mixin(targets = "androidx.compose.ui.text.input.TextInputSession", remap = false)
public abstract class ComposeTextInputSessionMixin implements  ComposeTextInputSessionAccessor {
	private Point imblocker$caretPos = Point.TOP_LEFT;
	private int imblocker$fontHeight = 0;
	
	@Inject(method = "notifyFocusedRect", at = @At("HEAD"))
	public void updateCaretPos(Rect focusedRect, CallbackInfoReturnable<Boolean> cir) {
		int currentFontHeight = (int) (focusedRect.getBottom() - focusedRect.getTop());
		Point currentCaretPos = new Point((int) focusedRect.getRight(), (int) focusedRect.getTop());

		boolean fontHeightChanged = false, caretPosChanged = false;
		if(fontHeightChanged = (currentFontHeight != imblocker$fontHeight)) {
			imblocker$fontHeight = currentFontHeight;
			IMManager.updateCompositionFontSize();
		}
		if(caretPosChanged = !currentCaretPos.equals(imblocker$caretPos)) {
			imblocker$caretPos = currentCaretPos;
		}
		if(fontHeightChanged || caretPosChanged) {
			IMManager.updateCaretPosition();
		}
	}
	
	public Point imblocker$getCaretPos() {
		return imblocker$caretPos;
	}
	
	public int imblocker$getFontHeight() {
		return imblocker$fontHeight;
	}
}
