package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import icyllis.modernui.graphics.Rect;
import icyllis.modernui.widget.EditText;
import io.github.reserveword.imblocker.common.IMManager;

@Mixin(value = EditText.class, remap = false)
public abstract class ModernUIEditTextMixin extends ModernUITextViewMixin {
	@Override
	public void imblocker$focusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect, CallbackInfo ci) {
		imblocker$onFocusChanged(gainFocus);
	}
	
	@Override
	public void imblocker$onLayout(boolean changed, int left, int top, int right, int bottom, CallbackInfo ci) {
		if(changed) {
			imblocker$onBoundsChanged();
		}
	}
	
	@Override
	public void imblocker$onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter, CallbackInfo ci) {
		if(isTrulyFocused()) {
			IMManager.updateCaretPosition();
		}
	}
}
