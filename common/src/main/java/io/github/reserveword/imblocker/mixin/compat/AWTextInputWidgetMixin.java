package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.TextStorageImpl;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIScrollView;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextView;
import com.apple.library.uikit.UIView;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Mixin(value = {UITextField.class, UITextView.class}, remap = false)
public abstract class AWTextInputWidgetMixin implements MinecraftFocusableWidget {
	
	@Shadow(remap = false)
	private TextStorageImpl storage;
	
	private float imblocker$scale = 1.0f;
	private Rectangle imblocker$bounds = Rectangle.EMPTY;
	private Point imblocker$caretPos = Point.TOP_LEFT;
	
	@Inject(method = "becomeFirstResponder", at = @At("TAIL"))
	public void focusGained(CallbackInfo ci) {
		imblocker$onFocusGained();
	}
	
	@Inject(method = "resignFirstResponder", at = @At("TAIL"))
	public void focusLost(CallbackInfo ci) {
		imblocker$onFocusLost();
	}
	
	@Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)  
	public void checkFocusTracking(UIEvent event, CallbackInfo ci) {
		if(FocusManager.isTrackingFocus) {
			if(storage.isFocused()) {
				FocusContainer.MINECRAFT.switchFocus(this);
			}
			ci.cancel();
		}
	}
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = 
			"Lcom/apple/library/impl/TextStorageImpl;render(Lcom/apple/library/coregraphics/CGPoint;Lcom/apple/library/coregraphics/CGGraphicsContext;)V",
			shift = At.Shift.AFTER))
	public void updateCaretPos(CGPoint point, CGGraphicsContext context, CallbackInfo ci) {
		if(!isTrulyFocused()) {
			return;
		}
		
		CGRect clip = context.boundingBoxOfClipPath();
		CGRect rawBounds = ((UIView) (Object) this).bounds().insetBy(1, 1, 1, 1);
		float currentScale = clip.height / rawBounds.height;
		if(imblocker$scale != currentScale) {
			imblocker$scale = currentScale;
			IMManager.updateCompositionFontSize();
		}
		
		Rectangle currentBounds = new Rectangle((int) clip.x, (int) clip.y, (int) clip.width, (int) clip.height);
		CGRect cursorRect = storage.cursorRect();
		CGPoint contentOffset = UIScrollView.class.isInstance(this) ? 
				((UIScrollView) (Object) this).contentOffset() : CGPoint.ZERO;
		Point currenetCaretPos = new Point(
				(int) (storage.offset.x + cursorRect.x - contentOffset.x), 
				(int) (storage.offset.y + cursorRect.y + (cursorRect.height - 1 - getFontHeight()) / 2 - contentOffset.y));
		
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
		return imblocker$caretPos.derive(getGuiScale() *  imblocker$scale);
	}
	
	@Override
	public int getFontHeight() {
		return (int) (8 * imblocker$scale);
	}
}
