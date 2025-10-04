package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import net.minecraft.client.input.CharacterEvent;

@Mixin(value = WTextField.class, remap = false)
public abstract class LibGuiTextFieldMixin extends LibGuiWidgetMixin implements MinecraftTextFieldWidget {
	
	@Shadow
	public static final int TEXT_PADDING_X = 4;
	
	@Shadow private boolean editable;
	
	@Shadow private String text;
	@Shadow private int scrollOffset;
	@Shadow private int cursor;
	
	private final SinglelineCursorInfo imblocker$cursorInfo = 
			new SinglelineCursorInfo(true, height, scrollOffset, cursor, text);
	
	@Override
	public void handleLocationChanged(int x, int y, CallbackInfo ci) {
		imblocker$onBoundsChanged();
	}

	@Override
	public void handleSizeChanged(int width, int height, CallbackInfo ci) {
		imblocker$onBoundsChanged();
	}
	
	@Inject(method = "onFocusGained", at = @At("TAIL"))
	public void onFocusGained(CallbackInfo ci) {
		imblocker$onFocusGained();
	}
	
	@Override
	public void onFocusLost(CallbackInfo ci) {
		imblocker$onFocusLost();
	}
	
	@Inject(method = "onCharTyped(Lnet/minecraft/class_11905;)Lio/github/cottonmc/cotton/gui/widget/data/InputResult;", 
			at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(CharacterEvent input, CallbackInfoReturnable<Object> cir) {
		if(FocusManager.isTrackingFocus) {
			if(editable) {
				FocusContainer.MINECRAFT.switchFocus(this);
				cir.setReturnValue(InputResult.PROCESSED);
			}else {
				cir.setReturnValue(InputResult.IGNORED);
			}
		}
	}
	
	@Inject(method = "setText", at = @At("TAIL"))
	public void onTextChanged(String s, CallbackInfo ci) {
		imblocker$onCursorChanged();
	}
	
	@Inject(method = "getSelection", at = @At("TAIL"))
	public void onGetSelection(CallbackInfoReturnable<String> cir) {
		imblocker$onCursorChanged();
	}
	
	@Inject(method = "scrollCursorIntoView", at = @At("TAIL"))
	public void onScroll(CallbackInfo ci) {
		imblocker$onCursorChanged();
	}
	
	@Override
	public boolean updateCursorInfo() {
		return imblocker$cursorInfo.updateCursorInfo(true, height, scrollOffset, cursor, text);
	}
	
	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return imblocker$cursorInfo;
	}
	
	@Override
	public int getPaddingX() {
		return TEXT_PADDING_X;
	}
}
