package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;

@Mixin(value = WTextField.class, remap = false)
public abstract class LibGuiTextFieldMixin extends LibGuiWidgetMixin implements MinecraftTextFieldWidget {
	
	@Shadow
	private boolean editable;
	
	@Shadow private String text;
	@Shadow private int scrollOffset;
	@Shadow private int cursor;
	
	@Inject(method = "onFocusGained", at = @At("TAIL"))
    public void onFocusGained(CallbackInfo ci) {
    	onMinecraftWidgetFocusGained();
    }
	
	@Override
    public void onFocusLost(CallbackInfo ci) {
    	onMinecraftWidgetFocusLost();
    }
	
	@Inject(method = "setEditable", at = @At("HEAD"), cancellable = true)
	public void setEditable(boolean editable, CallbackInfoReturnable<WTextField> cir) {
		if(this.editable == editable) {
			cir.setReturnValue((WTextField) (Object) this);
		}else if(isTrulyFocused()) {
			updateIMState();
		}
	}
	
	@Inject(method = "setText", at = @At("TAIL"))
	public void onTextChanged(String s, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Inject(method = "getSelection", at = @At("TAIL"))
	public void onGetSelection(CallbackInfoReturnable<String> cir) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Inject(method = "scrollCursorIntoView", at = @At("TAIL"))
	public void onScroll(CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public boolean getPreferredState() {
		return editable;
	}
	
	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return new SinglelineCursorInfo(true, height, scrollOffset, cursor, text);
	}
	
	@Override
	public int getPaddingX() {
		return WTextField.TEXT_PADDING_X;
	}
}
