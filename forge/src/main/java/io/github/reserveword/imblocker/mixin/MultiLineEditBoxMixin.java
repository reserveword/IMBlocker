package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftMultilineEditBoxWidget;
import io.github.reserveword.imblocker.common.gui.MultilineCursorInfo;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;

@Mixin(MultiLineEditBox.class)
public abstract class MultiLineEditBoxMixin extends AbstractScrollWidgetMixin implements MinecraftMultilineEditBoxWidget {
	
	@Shadow private Font font;
	@Shadow private MultilineTextField textField;
	
	@Override
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isFocused());
	}
	
	@Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if(IMBlockerCore.isTrackingFocus) {
			if(isFocused()) {
				FocusContainer.MINECRAFT.switchFocus(this);
				cir.setReturnValue(true);
			}else {
				cir.setReturnValue(false);
			}
		}
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
	public MultilineCursorInfo getCursorInfo() {
		int cursorLineIndex = textField.getLineAtCursor();
		return new MultilineCursorInfo(cursorLineIndex, scrollAmount(), 
				((StringViewAccessor) (Object) textField.getLineView(cursorLineIndex)).getBeginIndex(), 
				textField.cursor(), textField.value());
	}
}
