package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import androidx.compose.foundation.text.TextLayoutResultProxy;
import androidx.compose.ui.geometry.Rect;
import androidx.compose.ui.layout.LayoutCoordinates;
import androidx.compose.ui.layout.LayoutCoordinatesKt;
import androidx.compose.ui.text.input.OffsetMapping;
import androidx.compose.ui.text.input.TextFieldValue;
import androidx.compose.ui.text.input.TextInputSession;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.accessor.ComposeLegacyTextFieldStateAdapter;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import io.github.reserveword.imblocker.common.gui.mc.MinecraftFocusableWidget;

@Pseudo
@SuppressWarnings("deprecation")
@Mixin(targets = "androidx.compose.foundation.text.LegacyTextFieldState", remap = false)
public abstract class ComposeLegacyTextFieldStateMixin 
	implements MinecraftFocusableWidget, ComposeLegacyTextFieldStateAdapter {
	
	@Shadow
	private TextInputSession inputSession;

	private Rectangle imblocker$bounds = Rectangle.EMPTY;
	private Point imblocker$caretPos = Point.TOP_LEFT;
	private int imblocker$fontHeight = 0;
	
	@Shadow
	public abstract LayoutCoordinates getLayoutCoordinates();
	
	@Shadow
	public abstract TextLayoutResultProxy getLayoutResult();
	
	@Inject(method = "setInputSession", at = @At("TAIL"))
	public void onFocusChanged(CallbackInfo ci) {
		imblocker$onFocusChanged(inputSession != null);
	}
	
	@Override
	public void imblocker$updateCaretPos(TextFieldValue value, OffsetMapping offsetMapping) {
		// Compensate for the retainsScene semantics of PolyCompose.
		if(!isTrulyFocused() && inputSession != null) {
			imblocker$onFocusGained();
		}
		
		Rect boundsRect = LayoutCoordinatesKt.boundsInWindow(getLayoutCoordinates(), false);
		Rect clippedBoundsRect = LayoutCoordinatesKt.boundsInWindow(getLayoutCoordinates(), true);
		Rectangle currentBounds = new Rectangle(getGuiScale(), 
				clippedBoundsRect.getLeft(), clippedBoundsRect.getTop(), 
				clippedBoundsRect.getWidth(), clippedBoundsRect.getHeight());
		float scrollOffsetX = clippedBoundsRect.getLeft() - boundsRect.getLeft();
		float scrollOffsetY = clippedBoundsRect.getTop() - boundsRect.getTop();
		int transformedOffset = offsetMapping.originalToTransformed(
				(int) (((ComposeTextFieldValueAccessor) (Object) value).getSelection() >> 32));
		Rect cursorRect = getLayoutResult().getValue().getCursorRect(transformedOffset);
		Point currentCaretPos = new Point(getGuiScale(), 
				cursorRect.getRight() - scrollOffsetX, 
				cursorRect.getTop() - scrollOffsetY);
		int currentFontHeight = (int) cursorRect.getHeight();

		boolean boundsChanged = false, caretPosChanged = false, fontHeightChanged = false;
		if(boundsChanged = !currentBounds.equals(imblocker$bounds)) {
			imblocker$bounds = currentBounds;
		}
		if(caretPosChanged = !currentCaretPos.equals(imblocker$caretPos)) {
			imblocker$caretPos = currentCaretPos;
		}
		if(fontHeightChanged = (currentFontHeight != imblocker$fontHeight)) {
			imblocker$fontHeight = currentFontHeight;
			IMManager.updateCompositionFontSize();
		}
		if(boundsChanged || caretPosChanged || fontHeightChanged) {
			IMManager.updateCaretPosition();
		}
	}
	
	@Override
	public double getGuiScale() {
		// Compose calculates the density itself.
		return 1.0;
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		return imblocker$bounds;
	}
	
	@Override
	public Point getCaretPos() {
		return imblocker$caretPos;
	}
	
	@Override
	public int getFontHeight() {
		return imblocker$fontHeight;
	}
}
