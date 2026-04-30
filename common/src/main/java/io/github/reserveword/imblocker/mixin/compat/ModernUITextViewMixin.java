package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import icyllis.modernui.graphics.Rect;
import icyllis.modernui.text.Layout;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewParent;
import icyllis.modernui.widget.TextView;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import it.unimi.dsi.fastutil.floats.FloatArrayList;

@Mixin(value = TextView.class, remap = false)
public abstract class ModernUITextViewMixin extends View implements MinecraftFocusableWidget {
	public ModernUITextViewMixin() { super(null); }
	
	@Shadow private Layout mLayout;
	@Shadow private CharSequence mText;
	
	@Shadow public abstract int getSelectionStart();
	
	@Shadow public abstract int getTotalPaddingTop();
	
	@Shadow public abstract int getTotalPaddingLeft();

	@Shadow public abstract float getTextSize();
	
	@Inject(method = "onFocusChanged", at = @At("TAIL"))
	public void imblocker$focusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect, CallbackInfo ci) {}
	
	@Inject(method = "onLayout", at = @At("TAIL"))
	public void imblocker$onLayout(boolean changed, int left, int top, int right, int bottom, CallbackInfo ci) {}
	
	@Inject(method = "onTextChanged", at = @At("TAIL"))
	public void imblocker$onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter, CallbackInfo ci) {}
	
	@Override
	public void imblocker$onFocusGained() {
		MinecraftFocusableWidget.super.imblocker$onFocusGained();
		for(ViewParent parent = getParent(); parent != null; parent = parent.getParent()) {
			if(parent instanceof View) {
				/* Replacing scroll change listeners here seems offensive, however,
				 * it's the only way to listen for scroll operations currently
				 * since class icyllis.modernui.view.View is loaded too early and
				 * cannot be transformed with a regular mixin so we can't inject
				 * the callback to onScrollChanged method in that class conveniently. 
				 * Considering this listener is rarely registered in Minecraft environment, 
				 * may keep it as-is for now.
				 */
				((View) parent).setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
					imblocker$onBoundsChanged();
				});
			}
		}
	}
	
	@Override
	public void imblocker$onFocusLost() {
		MinecraftFocusableWidget.super.imblocker$onFocusLost();
		for(ViewParent parent = getParent(); parent != null; parent = parent.getParent()) {
			if(parent instanceof View) {
				((View) parent).setOnScrollChangeListener(null);
			}
		}
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		imblocker$onBoundsChanged();
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		int x = (int) getX() - getScrollX(), y = (int) getY() - getScrollY();
		for(ViewParent parent = getParent(); parent != null; parent = parent.getParent()) {
			if(parent instanceof View) {
				View _parent = (View) parent;
				x += _parent.getX() - _parent.getScrollX();
				y += _parent.getY() - _parent.getScrollY();
			}
		}
		return new Rectangle(x, y, getWidth(), getHeight());
	}
	
	@Override
	public double getGuiScale() {
		return 1.0;
	}
	
	@Override
	public Point getCaretPos() {
		FloatArrayList cursorInfo = new FloatArrayList();
		mLayout.getCursorPath(getSelectionStart(), cursorInfo, mText);
		return new Point((int) cursorInfo.getFloat(0) + getTotalPaddingLeft(), 
				(int) cursorInfo.getFloat(3) + getTotalPaddingTop() - getFontHeight());
	}
	
	@Override
	public int getFontHeight() {
		return (int) getTextSize();
	}
}
