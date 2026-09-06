package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import icyllis.modernui.text.Selection;
import icyllis.modernui.text.Spannable;
import icyllis.modernui.widget.EditText;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusManager;

@Mixin(value = Selection.class, remap = false)
public abstract class ModernUISelectionMixin {
	@Inject(method = "setSelection(Licyllis/modernui/text/Spannable;III)V", at = @At("TAIL"))
	private static void imblocker$onSelectionChanged(Spannable text, int start, int stop, int memory, CallbackInfo ci) {
		if(FocusManager.getFocusOwner() instanceof EditText) {
			IMManager.updateCaretPosition();
		}
	}
}
