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
import net.minecraft.client.gui.components.EditBox;

@Mixin(EditBox.class)
public abstract class TextFieldLegacyMixin extends AbstractWidgetMixin {
	
	@Shadow
	private boolean isEditable;
	
	@Shadow private Font font;
	@Shadow private boolean bordered;
	@Shadow private int displayPos;
	@Shadow private int cursorPos;
	@Shadow private String value;

	private boolean preferredEditState = true;

	private boolean preferredEnglishState = false;
	
	@Override
	public boolean isWidgetEditable() {
		return isEditable;
	}
	
	@Override
    public boolean getPreferredState() {
    	return isWidgetEditable() && preferredEditState;
    }
	
	@Override
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isFocused);
	}
	
	@Inject(method = "m_7207_", at = @At("TAIL"))
    public void focusBeChanged(boolean isFocused, CallbackInfo ci) {
    	onMinecraftWidgetFocusChanged(isFocused);
    }
	
	@Inject(method = "onValueChange", at = @At("TAIL"))
    public void onTextChanged(String newValue, CallbackInfo ci) {
    	IMManager.updateCompositionWindowPos();
    }
	
	@Inject(method = "setEditable", at = @At("HEAD"), cancellable = true)
    public void setEditable(boolean editable, CallbackInfo ci) {
		if(this.isEditable != editable) {
    		this.isEditable = editable;
    		if(isTrulyFocused()) {
    			updateIMState();
    		}
    	}
		ci.cancel();
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
    	int caretX = (bordered ? 4 : 0) + font.width(value.substring(displayPos, cursorPos));
    	return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, (height - 8) / 2);
    }
}
