package io.github.reserveword.imblocker.mixin.neoforge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import net.minecraft.client.gui.components.EditBox;

@Mixin(EditBox.class)
public abstract class TextFieldMixin implements MinecraftFocusableWidget {
	
	@Shadow
	private boolean isEditable;

	private boolean preferredEnglishState = false;
	
	@Override
	public boolean isWidgetEditable() {
		return isEditable;
	}
	
	@Inject(method = "setFocused", at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onFocusChanged(isFocused);
	}
	
	@Inject(method = "setEditable", at = @At("HEAD"))
    public void setEditable(boolean editable, CallbackInfo ci) {
		if(this.isEditable != editable) {
    		this.isEditable = editable;
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
}
