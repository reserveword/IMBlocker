package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.CursorInfo;
import io.github.reserveword.imblocker.common.gui.MinecraftMultilineEditBoxWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;

@Mixin(MultiLineEditBox.class)
public abstract class MultiLineEditBoxMixin extends AbstractScrollWidgetMixin implements MinecraftMultilineEditBoxWidget {
	
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
	public void onScroll(double scroll, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public Point getCaretPos() {
		int cursorLineIndex = textField.getLineAtCursor();
		return getCaretPos(new CursorInfo(true, height, cursorLineIndex, scrollAmount(), 
				((StringViewAccessor) (Object) textField.getLineView(cursorLineIndex)).getBeginIndex(), 
				textField.cursor(), textField.value()));
	}
}
