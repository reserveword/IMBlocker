package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.CursorInfo;
import io.github.reserveword.imblocker.common.gui.MinecraftMultilineEditBoxWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.widget.EditBoxWidget;

@Mixin(EditBoxWidget.class)
public abstract class EditBoxWidgetMixin extends ScrollableWidgetMixin implements MinecraftMultilineEditBoxWidget {
	
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
	public void onScroll(double scrollY, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public CursorInfo getCursorInfo() {
		int cursorLineIndex = editBox.getCurrentLineIndex();
		return new CursorInfo(true, height, cursorLineIndex, getScrollY(), 
				((SubstringAccessor) (Object) editBox.getLine(cursorLineIndex)).getBeginIndex(), 
				editBox.getCursor(), editBox.getText());
	}
}
