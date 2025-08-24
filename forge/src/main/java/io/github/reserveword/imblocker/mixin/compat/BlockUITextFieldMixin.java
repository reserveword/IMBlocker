package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ldtteam.blockui.controls.TextField;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;

@Mixin(value = TextField.class, remap = false)
public abstract class BlockUITextFieldMixin extends BlockUIPaneMixin implements MinecraftTextFieldWidget {
	
	@Shadow protected String text;
	@Shadow protected int scrollOffset = 0;
	@Shadow protected int cursorPosition = 0;
	
	private double imblocker$guiScale;
	private final SinglelineCursorInfo imblocker$cursorInfo = 
			new SinglelineCursorInfo(true, height, scrollOffset, cursorPosition, text);
	
	@Inject(method = "onFocus", at = @At("TAIL"))
	public void focusGained(CallbackInfo ci) {
		imblocker$onFocusGained();
	}
	
	@Override
	public void focusLost(CallbackInfo ci) {
		imblocker$onFocusLost();
	}
	
	@Inject(method = "onKeyTyped", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(char c, int key, CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.isTrackingFocus) {
			FocusContainer.MINECRAFT.switchFocus(this);
			cir.setReturnValue(true);
		}
	}
	
	@Inject(method = "onUpdate", at = @At("TAIL"))
	public void onUpdate(CallbackInfo ci) {
		double currentGuiScale = super.getGuiScale();
		if(imblocker$guiScale != currentGuiScale) {
			imblocker$guiScale = currentGuiScale;
			IMManager.updateCompositionFontSize();
		}
	}
	
	@Inject(method = "setCursorPosition", at = @At("TAIL"))
	public void onCursorChanged(int pos, CallbackInfo ci) {
		imblocker$onCursorChanged();
	}
	
	@Inject(method = "setSelectionEnd", at = @At("TAIL"))
	public void onSelectionChanged(int pos, CallbackInfo ci) {
		imblocker$onCursorChanged();
	}
	
	@Override
	public boolean updateCursorInfo() {
		return imblocker$cursorInfo.updateCursorInfo(true, height, scrollOffset, cursorPosition, text);
	}
	
	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return imblocker$cursorInfo;
	}
	
	@Override
	public double getGuiScale() {
		return imblocker$guiScale;
	}
}
