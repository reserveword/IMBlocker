package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.Point;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldMixin extends ClickableWidgetMixin {
	
	@Shadow
	private boolean editable;
	
	@Shadow private TextRenderer textRenderer;
	@Shadow private boolean drawsBackground;
	@Shadow private int firstCharacterIndex;
	@Shadow private int selectionStart;
	@Shadow private String text;
	
	private boolean preferredEditState = true;
	
	private boolean preferredEnglishState = false;
    
    @Override
    public boolean isWidgetEditable() {
    	return editable;
    }
    
    @Override
    public boolean getPreferredState() {
    	return isWidgetEditable() && preferredEditState;
    }
    
    @Inject(method = {"setFocused", "method_25365"}, at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
    	onFocusChanged(isFocused);
    }
    
    @Inject(method = "onChanged", at = @At("TAIL"))
    public void onTextChanged(String newText, CallbackInfo ci) {
    	IMManager.updateCompositionWindowPos();
    }
    
    @Overwrite
    public void setEditable(boolean editable) {
    	if(this.editable != editable) {
    		this.editable = editable;
    		if(isTrulyFocused()) {
    			updateIMState();
    		}
    	}
    }
    
    @Override
    public void setPreferredEditState(boolean preferredEditState) {
    	if(this.preferredEditState != preferredEditState) {
    		this.preferredEditState = preferredEditState;
    		if(isTrulyFocused()) {
    			updateIMState();
    		}
    	}
    }
    
    @Override
    public void setPreferredEnglishState(boolean state) {
    	if(preferredEnglishState != state) {
    		preferredEnglishState = state;
    		if(isTrulyFocused()) {
    			updateEnglishState();
    		}
    	}
    }
    
    @Override
    public boolean getPreferredEnglishState() {
    	return preferredEnglishState;
    }
    
    @Override
    public Point getCaretPos() {
    	int caretX = (drawsBackground ? 4 : 0) + textRenderer.getWidth(text.substring(firstCharacterIndex, selectionStart));
    	return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, (height - 8) / 2);
    }
}
