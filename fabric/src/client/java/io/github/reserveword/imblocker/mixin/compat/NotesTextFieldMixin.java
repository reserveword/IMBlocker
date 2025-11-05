package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.mixin.ClickableWidgetMixin;

@Pseudo
@Mixin(targets = "com.chaosthedude.notes.gui.NotesTextField")
public abstract class NotesTextFieldMixin extends ClickableWidgetMixin {
	
	@Shadow
	private int margin;
	
	private Point imblocker$caretPos = Point.TOP_LEFT;
	
	@Shadow
	public abstract String getCurrentLine();
	
	@Shadow
	public abstract int getCursorX();
	
	@Shadow
	public abstract int getRenderSafeCursorY();
	
	@Override
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		imblocker$onFocusChanged(isFocused);
	}
	
	@Inject(method = {"charTyped", "method_25400"}, at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.isTrackingFocus) {
			if(isFocused()) {
				FocusContainer.MINECRAFT.switchFocus(this);
				cir.setReturnValue(true);
			}else {
				cir.setReturnValue(false);
			}
		}
	}
	
	@Inject(method = "renderCursor", at = @At("TAIL"))
	public void updateCaretPos(CallbackInfo ci) {
		if(!isTrulyFocused()) {
			return;
		}
		
		int caretX = margin + MinecraftClientAccessor.INSTANCE.getStringWidth(
				StringUtil.getSubstring(getCurrentLine(), 0, getCursorX()));
		int caretY = margin + getRenderSafeCursorY() * 9;
		Point currentCaretPos = new Point(getGuiScale(), caretX, caretY);
		if(!imblocker$caretPos.equals(currentCaretPos)) {
			imblocker$caretPos = currentCaretPos;
			IMManager.updateCompositionWindowPos();
		}
	}
	
	@Override
	public Point getCaretPos() {
		return imblocker$caretPos;
	}
}
