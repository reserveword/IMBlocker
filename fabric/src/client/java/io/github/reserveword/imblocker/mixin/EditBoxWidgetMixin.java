package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.Point;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.widget.EditBoxWidget;

@Mixin(EditBoxWidget.class)
public abstract class EditBoxWidgetMixin extends ScrollableWidgetMixin {
	
	@Shadow private TextRenderer textRenderer;
	@Shadow private EditBox editBox;

	@Override
	public boolean isWidgetEditable() {
		return true;
	}
	
	@Override
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isFocused);
	}
	
	@Inject(method = "onCursorChange", at = @At("TAIL"))
	public void onCursorChange(CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public Point getCaretPos() {
		int cursorLineIndex = editBox.getCurrentLineIndex();
		int lineY = y() + 4 + cursorLineIndex * 9;
		if(isVisible(lineY, lineY + 9)) {
			int beginIndex = ((SubstringAccessor) (Object) editBox.getLine(cursorLineIndex)).getBeginIndex();
			int caretX = 4 + textRenderer.getWidth(editBox.getText().substring(beginIndex, editBox.getCursor()));
			return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, lineY - y() - (int) getScrollY());
		}else {
			return new Point(FocusContainer.getMCGuiScaleFactor(), 4, (height - 8) / 2);
		}
	}
}
