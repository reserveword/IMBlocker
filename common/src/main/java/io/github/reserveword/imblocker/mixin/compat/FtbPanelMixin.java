package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftblibrary.ui.MultilineTextBox;
import dev.ftb.mods.ftblibrary.ui.Panel;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.FocusableObject;
import io.github.reserveword.imblocker.common.gui.MinecraftMultilineEditBoxWidget;

@Mixin(value = Panel.class, remap = false)
public abstract class FtbPanelMixin extends FtbWidgetMixin {
	
	@Override
	public boolean isValidLayoutWidget() {
		return true;
	}
	
	@Inject(method = "setScrollY", at = @At("TAIL"))
	public void onScroll(double scroll, CallbackInfo ci) {
		FocusableObject focusOwner = FocusManager.getFocusOwner();
		if(focusOwner instanceof MultilineTextBox) {
			((MinecraftMultilineEditBoxWidget) focusOwner).imblocker$onCursorChanged(); 
		}
	}
}
