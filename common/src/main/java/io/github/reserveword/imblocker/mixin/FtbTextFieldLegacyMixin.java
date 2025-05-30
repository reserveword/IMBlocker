package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftblibrary.ui.TextBox;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.Point;

@Mixin(value = TextBox.class, remap = false)
public abstract class FtbTextFieldLegacyMixin extends FtbWidgetMixin {
	
	@Shadow private String text;
	@Shadow private int lineScrollOffset;
	@Shadow private int cursorPosition;
	
	@Override
	public boolean isWidgetEditable() {
		return true;
	}
	
	@Inject(method = "setFocused", at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
    	onMinecraftWidgetFocusChanged(isFocused);
    }
    
    @Inject(method = "onClosed", at = @At("TAIL"))
    public void cancelFocus(CallbackInfo ci) {
    	onMinecraftWidgetFocusLost();
    }
    
    @Inject(method = "setSelectionPos", at = @At("TAIL"))
    public void onCursorPosChanged(int position, CallbackInfo ci) {
    	IMManager.updateCompositionWindowPos();
    }
    
    @Override
    public Point getCaretPos() {
    	int caretX = 4 + MinecraftClientAccessor.INSTANCE.getStringWidth(
    			StringUtil.getSubstring(text, lineScrollOffset, cursorPosition));
    	return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, (height - 8) / 2);
    }
}
