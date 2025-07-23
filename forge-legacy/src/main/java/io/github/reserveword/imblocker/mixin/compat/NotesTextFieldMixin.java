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
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Pseudo
@Mixin(targets = "com.chaosthedude.notes.gui.NotesTextField", remap = false)
public abstract class NotesTextFieldMixin implements MinecraftFocusableWidget {
	
	@Shadow
	private int margin;
	
	@Shadow public int xPosition;
	@Shadow public int yPosition;
	@Shadow public int field_230708_k_; //width
	@Shadow public int field_230709_l_; //height
	
	private Point imblocker$caretPos = Point.TOP_LEFT;
	
	@Shadow
	public abstract boolean isFocused();
	
	@Shadow
	public abstract String getCurrentLine();
	
	@Shadow
	public abstract int getCursorX();
	
	@Shadow
	public abstract int getRenderSafeCursorY();
	
	@Inject(method = "setFocused", at = @At("TAIL"))
	public void focusChanged(boolean focused, CallbackInfo ci) {
		imblocker$onFocusChanged(focused);
	}
	
	@Inject(method = "func_231042_a_", at = @At("HEAD"), cancellable = true)
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
	public Rectangle getBoundsAbs() {
		return new Rectangle(getGuiScale(), xPosition, yPosition, field_230708_k_, field_230709_l_);
	}
	
	@Override
	public Point getCaretPos() {
		return imblocker$caretPos;
	}
}
