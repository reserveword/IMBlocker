package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.input.KeyModifiers;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.FtbTextInputWidget;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;

@Mixin(value = TextBox.class, remap = false)
public abstract class FtbTextFieldMixin extends FtbWidgetMixin 
	implements MinecraftTextFieldWidget, FtbTextInputWidget {
	
	@Shadow private String text;
	
	@Shadow(aliases = "lineScrollOffset")
	private int displayPos;
	
	@Shadow(aliases = "cursorPosition")
	private int cursorPos;
	
	@Shadow
	private boolean isFocused;
	
	private final SinglelineCursorInfo imblocker$cursorInfo = 
			new SinglelineCursorInfo(true, height, displayPos, cursorPos, text);
	
	@Override
	public void handleBoundsChanged() {
		imblocker$onBoundsChanged();
	}

	@Inject(method = "setFocused", at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		imblocker$onFocusChanged(this.isFocused);
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

	@Override
	@Inject(method = "onClosed", at = @At("TAIL"), require = 0)
	public void cancelFocus(CallbackInfo ci) {
		imblocker$onFocusLost();
	}

	@Inject(method = {"scrollTo", "setSelectionPos"}, at = @At("TAIL"))
	public void onCursorPosChanged(int pos, CallbackInfo ci) {
		imblocker$onCursorChanged();
	}
	
	@Override
	public boolean updateCursorInfo() {
		return imblocker$cursorInfo.updateCursorInfo(true, height, displayPos, cursorPos, text);
	}

	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return imblocker$cursorInfo;
	}
}
