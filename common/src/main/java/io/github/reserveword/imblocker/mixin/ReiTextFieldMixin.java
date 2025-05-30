package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import me.shedaniel.rei.impl.client.gui.widget.basewidgets.TextFieldWidget;

@Pseudo
@Mixin(value = TextFieldWidget.class, remap = false)
public abstract class ReiTextFieldMixin implements MinecraftFocusableWidget {
	
    @Shadow
    protected boolean editable;
    
    @Shadow
    private me.shedaniel.math.Rectangle bounds;
    
    @Shadow private boolean hasBorder;
    @Shadow protected int firstCharacterIndex;
    @Shadow protected int cursorPos;
    @Shadow private String text;
    
    @Override
    public boolean isWidgetEditable() {
    	return editable;
    }
    
    @Inject(method = {"setFocused", "method_25365", "m_93692_"}, at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
    	onMinecraftWidgetFocusChanged(isFocused);
    }
    
    @Inject(method = "onChanged", at = @At("TAIL"))
    public void onTextChanged(String newText, CallbackInfo ci) {
    	IMManager.updateCompositionWindowPos();
    }
    
    @Inject(method = "moveCursorTo", at = @At("TAIL"))
    public void onMoveCursor(int cursor, CallbackInfo ci) {
    	IMManager.updateCompositionWindowPos();
    }
    
    @Overwrite(aliases = {"setEditable"})
    public void setIsEditable(boolean editable) {
    	if(this.editable != editable) {
    		this.editable = editable;
    		if(isTrulyFocused()) {
    			updateIMState();
    		}
    	}
    }
    
    @Override
    public Rectangle getBoundsAbs() {
    	return new Rectangle(FocusContainer.getMCGuiScaleFactor(), 
    			bounds.x, bounds.y, bounds.width, bounds.height);
    }
    
    @Override
    public Point getCaretPos() {
    	int caretX = (hasBorder ? 4 : 0) + MinecraftClientAccessor.INSTANCE.getStringWidth(
    			StringUtil.getSubstring(text, firstCharacterIndex, cursorPos));
    	return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, (bounds.height - 8) / 2);
    }
}
