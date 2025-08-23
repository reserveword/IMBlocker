package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.ftb.mods.ftblibrary.ui.MultilineTextBox;
import dev.ftb.mods.ftblibrary.ui.input.KeyModifiers;
import io.github.reserveword.imblocker.common.accessor.FtbMultilineTextFieldAccessor;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftMultilineEditBoxWidget;
import io.github.reserveword.imblocker.common.gui.MultilineCursorInfo;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Mixin(value = MultilineTextBox.class, remap = false)
public abstract class FtbMultilineTextBoxMixin extends FtbWidgetMixin implements MinecraftMultilineEditBoxWidget {
	
	@Shadow
	private boolean isFocused;
	
	private final MultilineCursorInfo imblocker$cursorInfo = new MultilineCursorInfo(0, 0, 0, 0, "");
	
	@Inject(method = "setFocused", at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		imblocker$onFocusChanged(isFocused);
	}
	
	@Override
    public void cancelFocus(CallbackInfo ci) {
    	imblocker$onFocusLost();
    }
	
	@Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(char c, KeyModifiers modifiers, CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.isTrackingFocus) {
			if(isFocused) {
				FocusContainer.MINECRAFT.switchFocus(this);
				cir.setReturnValue(true);
			}else {
				cir.setReturnValue(false);
			}
		}
	}
	
	@Inject(method = "scrollToCursor", at = @At("TAIL"))
	public void onCursorChange(CallbackInfo ci) {
		imblocker$onCursorChanged();
	}
	
	@Inject(method = "recalculateHeight", at = @At("TAIL"))
	public void onRecalculateHeight(CallbackInfo ci) {
		imblocker$onCursorChanged();
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		if(parent != null) {
			return new Rectangle(getGuiScale(), getAbsoluteX(), getAbsoluteY(), width, parent.height);
		}
		return super.getBoundsAbs();
	}
	
	@Override
	public boolean updateCursorInfo() {
		double scrollY = parent != null ? parent.getScrollY() : 0;
		return ((FtbMultilineTextFieldAccessor) this).updateCursorInfo(imblocker$cursorInfo, scrollY);
	}
	
	@Override
	public MultilineCursorInfo getCursorInfo() {
		return imblocker$cursorInfo;
	}
}
