package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftblibrary.ui.MultilineTextBox;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.Point;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.MultilineTextField;

@Mixin(value = MultilineTextBox.class, remap = false)
public abstract class FtbMultilineTextBoxMixin extends FtbWidgetMixin {
	
	@Shadow
	MultilineTextField textField;
	
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
	
	@Shadow
	abstract boolean withinContentArea(int y1, int y2);
	
	@Override
	public Point getCaretPos() {
		int cursorLineIndex = textField.getLineAtCursor();
		int scrollY = parent != null ? (int) parent.getScrollY() : 0;
		int lineY = getAbsoluteY() + 4 + cursorLineIndex * 9;
		if(withinContentArea(lineY, lineY + 9)) {
			Font font = Minecraft.getInstance().font;
			int beginIndex = ((StringViewAccessor) (Object) textField.getLineView(cursorLineIndex)).getBeginIndex();
			int caretX = 4 + font.width(textField.value().substring(beginIndex, textField.cursor()));
			return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, lineY - getAbsoluteY() - scrollY);
		}else {
			return new Point(FocusContainer.getMCGuiScaleFactor(), 4, (height - 8) / 2);
		}
	}
}
