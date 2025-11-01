package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.TextStorageImpl;
import com.apple.library.uikit.UIFont;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.accessor.AWCGGraphicsContextAccessor;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Pseudo
@Mixin(value = TextStorageImpl.class, remap = false)
public abstract class AWTextStorageMixin implements MinecraftFocusableWidget {
	
	@Shadow
	private boolean isFocused;
	
	@Shadow private UIFont cachedFont;
	@Shadow private CGPoint offset;
	@Shadow private CGRect cursorRect;
	
	private float imblocker$scale = 1.0f;
	private Rectangle imblocker$bounds = Rectangle.EMPTY;
	private Point imblocker$caretPos = Point.TOP_LEFT;
	
	@Inject(method = "setFocused", at = @At("TAIL"))
	public void focusChanged(boolean focused, CallbackInfo ci) {
		imblocker$onFocusChanged(focused);
	}
	
	@Inject(method = "insertText", at = @At("HEAD"), cancellable = true)  
	public void checkFocusTracking(String text, CallbackInfo ci) {
		if(FocusManager.isTrackingFocus) {
			if(isFocused) {
				FocusContainer.MINECRAFT.switchFocus(this);
			}
			ci.cancel();
		}
	}
	
	@Inject(method = "isAllowedChatCharacter", at = @At("HEAD"), cancellable = true)
	private void skipWhenTrackingFocus(char c, CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.isTrackingFocus) {
			cir.setReturnValue(true);
		}
	}
	
	@Inject(method = "render", at = @At("TAIL"))
	public void updateCaretPos(CGPoint point, CGGraphicsContext context, CallbackInfo ci) {
		imblocker$scale = ((AWCGGraphicsContextAccessor) context).imblocker$getScale();
		CGRect clip = context.boundingBoxOfClipPath();
		Rectangle currentBounds = new Rectangle((int) clip.x, (int) clip.y, (int) clip.width, (int) clip.height);
		Point currenetCaretPos = new Point(
				(int) (offset.x + cursorRect.x), 
				(int) (offset.y + cursorRect.y + (cursorRect.height - 1 - getFontHeight()) / 2));
		
		boolean boundsChanged = false, caretPosChanged = false;
		if(boundsChanged = !currentBounds.equals(imblocker$bounds)) {
			imblocker$bounds = currentBounds;
		}
		if(caretPosChanged = !currenetCaretPos.equals(imblocker$caretPos)) {
			imblocker$caretPos = currenetCaretPos;
		}
		if(boundsChanged || caretPosChanged) {
			IMManager.updateCompositionWindowPos();
		}
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		return imblocker$bounds.derive(getGuiScale());
	}
	
	@Override
	public Point getCaretPos() {
		return imblocker$caretPos.derive(getGuiScale() * imblocker$scale);
	}
	
	@Override
	public int getFontHeight() {
		return (int) (MinecraftFocusableWidget.super.getFontHeight() * imblocker$scale);
	}
}
