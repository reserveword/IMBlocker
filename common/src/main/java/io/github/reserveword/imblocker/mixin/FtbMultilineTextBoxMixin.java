package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftblibrary.ui.MultilineTextBox;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.FtbMultilineTextFieldAccessor;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.CursorInfo;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MathHelper;
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
	
	@Inject(method = "recalculateHeight", at = @At("TAIL"))
	public void onRecalculateHeight(CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public Point getCaretPos() {
		CursorInfo cursorInfo = ((FtbMultilineTextFieldAccessor) this).getCursorInfo();
		
		double scrollY = parent != null ? parent.getScrollY() : 0;
		int visibleHeight = parent != null ? parent.height : height;
		int lineY = (int) (4 + cursorInfo.cursorLineIndex * 9 - scrollY);
		
		int caretX = 4 + MinecraftClientAccessor.INSTANCE.getStringWidth(
				StringUtil.getSubstring(cursorInfo.text, cursorInfo.cursorLineBeginIndex, cursorInfo.cursor));
		int caretY = MathHelper.clamp(lineY, 0, visibleHeight - 4);
		
		return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, caretY);
	}
}
