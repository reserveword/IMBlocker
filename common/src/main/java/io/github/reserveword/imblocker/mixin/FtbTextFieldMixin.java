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
public abstract class FtbTextFieldMixin extends FtbWidgetMixin {
	
	@Shadow private String text;
	@Shadow private int displayPos;
	@Shadow private int cursorPos;
    
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
    
    @Inject(method = "scrollTo", at = @At("TAIL"))
    public void onCursorPosChanged(int pos, CallbackInfo ci) {
    	IMManager.updateCompositionWindowPos();
    }
    
    @Override
    public Point getCaretPos() {
    	int caretX = 4 + MinecraftClientAccessor.INSTANCE.getStringWidth(
    			StringUtil.getSubstring(text, displayPos, cursorPos));
    	return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, (height - 8) / 2);
    }
}
