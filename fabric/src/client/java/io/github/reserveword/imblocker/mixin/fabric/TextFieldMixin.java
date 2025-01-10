package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldMixin implements MinecraftFocusableWidget {
	
	@Shadow
	private boolean editable;
	
	private boolean preferredEnglishState = false;

	private boolean isTrulyFocused = false;
    public void setTrulyFocused(boolean isTrulyFocused) { this.isTrulyFocused = isTrulyFocused; }
    
    @Override
    public boolean isWidgetEditable() {
    	return editable;
    }
    
    @Inject(method = {"setFocused", "method_25365"}, at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
    	onFocusChanged(isFocused);
    }
    
    @Inject(method = "setEditable", at = @At("HEAD"))
    public void updateIMState(boolean editable, CallbackInfo ci) {
    	if(this.editable != editable) {
    		this.editable = editable;
    		if(isTrulyFocused) {
    			IMManager.updateIMState(editable, preferredEnglishState);
    		}
    	}
    }
    
    @Override
    public void setPreferredEnglishState(boolean state) {
    	if(preferredEnglishState != state) {
    		preferredEnglishState = state;
    		if(isTrulyFocused && editable) {
    			IMManager.setEnglishState(preferredEnglishState);
    		}
    	}
    }
    
    @Override
    public boolean getPreferredEnglishState() {
    	return preferredEnglishState;
    }
}
