package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.Point;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;

@Mixin(MultiLineEditBox.class)
public abstract class MultiLineEditBoxMixin extends AbstractScrollWidgetMixin {
	
	@Shadow private Font font;
	@Shadow private MultilineTextField textField;
	
	@Override
	public boolean isWidgetEditable() {
		return true;
	}
	
	@Override
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isFocused);
	}
	
	@Inject(method = "scrollToCursor", at = @At("TAIL"))
	public void onCursorChange(CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public Point getCaretPos() {
		int cursorLineIndex = textField.getLineAtCursor();
		int lineY = y() + 4 + cursorLineIndex * 9;
		if(withinContentAreaTopBottom(lineY, lineY + 9)) {
			int beginIndex = ((MultilineTextFieldAccessor) textField).getDisplayLines().get(cursorLineIndex).getBeginIndex();
			int caretX = 4 + font.width(textField.value().substring(beginIndex, textField.cursor()));
			return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, lineY - y() - (int) scrollAmount());
		}else {
			return new Point(FocusContainer.getMCGuiScaleFactor(), 4, (height - 8) / 2);
		}
	}
}
