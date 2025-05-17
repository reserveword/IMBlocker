package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
	
	@Shadow
	abstract void scrollToCursor(); 
	
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
		FtbMultilineTextFieldAccessor accessor = (FtbMultilineTextFieldAccessor) this;
		int cursorLineIndex = accessor.getCursorLineIndex();
		double scrollY = parent != null ? parent.getScrollY() : 0;
		int visibleHeight = parent != null ? parent.height : height;
		int lineY = (int) (4 + cursorLineIndex * 9 - scrollY);
		int beginIndex = accessor.getLineBeginIndex(cursorLineIndex);
		
		int caretX = 4 + MinecraftClientAccessor.instance.getStringWidth(
				accessor.getText().substring(beginIndex, accessor.getCursor()));
		int caretY;
		if(lineY < 0) {
			caretY = 4;
		}else if(lineY > visibleHeight) {
			caretY = visibleHeight - 4;
		}else {
			caretY = lineY;
		}
		return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, caretY);
	}
}
