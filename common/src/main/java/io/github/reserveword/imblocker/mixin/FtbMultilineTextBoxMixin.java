package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftblibrary.ui.MultilineTextBox;
import io.github.reserveword.imblocker.common.FtbMultilineTextFieldAccessor;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.Point;

@Mixin(value = MultilineTextBox.class, remap = false)
public abstract class FtbMultilineTextBoxMixin extends FtbWidgetMixin {
	
	@Override
	public boolean isWidgetEditable() {
		return true;
	}
	
	@Inject(method = "setFocused", at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isFocused);
	}
	
	@Override
    public void cancelFocus(CallbackInfo ci) {
    	onMinecraftWidgetFocusLost();
    }
	
	@Inject(method = "scrollToCursor", at = @At("TAIL"))
	public void onCursorChange(CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public Point getCaretPos() {
		FtbMultilineTextFieldAccessor accessor = (FtbMultilineTextFieldAccessor) this;
		int cursorLineIndex = accessor.getCursorLineIndex(); 
		int yAbs = getAbsoluteY();
		int scrollY = parent != null ? (int) parent.getScrollY() : 0;
		int visibleHeight = parent != null ? parent.height : height;
		int lineY = yAbs + 4 + cursorLineIndex * 9;
		boolean isVisible = lineY + 9 - scrollY >= yAbs && lineY - scrollY <= yAbs + visibleHeight;
		if(isVisible) {
			int beginIndex = accessor.getLineBeginIndex(cursorLineIndex);
			int caretX = 4 + MinecraftClientAccessor.instance.getStringWidth(
					accessor.getText().substring(beginIndex, accessor.getCursor()));
			return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, lineY - yAbs - scrollY);
		}else {
			return new Point(FocusContainer.getMCGuiScaleFactor(), 4, (visibleHeight - 8) / 2);
		}
	}
}
