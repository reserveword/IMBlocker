package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldMixin implements MinecraftFocusableWidget {
	
	@Shadow
	private boolean editable;
	
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
}
