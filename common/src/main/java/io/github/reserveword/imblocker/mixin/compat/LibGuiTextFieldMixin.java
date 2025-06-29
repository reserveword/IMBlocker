package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;

@Mixin(value = WTextField.class, remap = false)
public abstract class LibGuiTextFieldMixin extends LibGuiWidgetMixin implements MinecraftTextFieldWidget {
	
	@Shadow(aliases = {"OFFSET_X_TEXT"})
	public static final int TEXT_PADDING_X = 4;
	
	@Shadow private String text;
	@Shadow private int scrollOffset;
	@Shadow private int cursor;
	
	@Unique
	private static final Object PROCESSED_INPUTRESULT;
	
	@Inject(method = "onFocusGained", at = @At("TAIL"))
	public void onFocusGained(CallbackInfo ci) {
		onMinecraftWidgetFocusGained();
	}
	
	@Override
	public void onFocusLost(CallbackInfo ci) {
		onMinecraftWidgetFocusLost();
	}
	
	@Inject(method = "onCharTyped(C)V", at = @At("HEAD"), cancellable = true, require = 0)
	public void checkFocusTracking(char c, CallbackInfo ci) {
		if(IMBlockerCore.isTrackingFocus) {
			FocusContainer.MINECRAFT.switchFocus(this);
			ci.cancel();
		}
	}
	
	@Inject(method = "onCharTyped(C)Lio/github/cottonmc/cotton/gui/widget/data/InputResult;", 
			at = @At("HEAD"), cancellable = true, require = 0)
	public void checkFocusTracking(char c, CallbackInfoReturnable<Object> cir) {
		if(IMBlockerCore.isTrackingFocus) {
			FocusContainer.MINECRAFT.switchFocus(this);
			cir.setReturnValue(PROCESSED_INPUTRESULT);
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
	public SinglelineCursorInfo getCursorInfo() {
		return new SinglelineCursorInfo(true, height, scrollOffset, cursor, text);
	}
	
	@Override
	public int getPaddingX() {
		return TEXT_PADDING_X;
	}
	
	static {
		Object processedInputResult = null;
		if(IMBlockerCore.isGameVersionReached(762/*1.19.4*/)) {
			try {
				Class<?> inputResultClass = Class.forName(
						"io.github.cottonmc.cotton.gui.widget.data.InputResult");
				processedInputResult = ReflectionUtil.getFieldValue(
						inputResultClass, null, null, "PROCESSED");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		PROCESSED_INPUTRESULT = processedInputResult;
	}
}
