package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;

@Pseudo
@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options.client.gui.widget.TextFieldWidget", remap = false)
public abstract class RSOTextFieldMixin extends RSOBaseWidgetMixin implements MinecraftTextFieldWidget {
	@Shadow protected String text;
	@Shadow private int firstCharacterIndex;
	@Shadow private int selectionStart;
	
	private final SinglelineCursorInfo imblocker$cursorInfo = 
			new SinglelineCursorInfo(true, 0, firstCharacterIndex, selectionStart, text);
	
	@Shadow
	public abstract boolean canConsumeTextInput();
	
	@Override
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		imblocker$onFocusChanged(canConsumeTextInput());
	}
	
	@Inject(method = { "charTyped", "method_25400" }, at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.isTrackingFocus) {
			if(canConsumeTextInput()) {
				FocusContainer.MINECRAFT.switchFocus(this);
				cir.setReturnValue(true);
			}else {
				cir.setReturnValue(false);
			}
		}
	}
	
	@Inject(method = "setSelectionStart", at = @At("TAIL"))
	public void onCursorChanged(int cursor, CallbackInfo ci) {
		imblocker$onCursorChanged();
	}
	
	@Inject(method = "clearText", at = @At("TAIL"))
	public void onTextCleared(CallbackInfo ci) {
		imblocker$onCursorChanged();
	}
	
	@Override
	public boolean updateCursorInfo() {
		return imblocker$cursorInfo.updateCursorInfo(true, getHeight(), firstCharacterIndex, selectionStart, text);
	}
	
	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return imblocker$cursorInfo;
	}
	
	@Override
	public int getPaddingX() {
		return 6;
	}
}
