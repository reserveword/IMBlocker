package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;

@Pseudo
@Mixin(targets = {
        "me.shedaniel.rei.impl.client.gui.widget.basewidgets.TextFieldWidget",
        "me.shedaniel.rei.gui.widget.TextFieldWidget"
}, remap = false)
public abstract class ReiTextFieldMixin implements MinecraftFocusableWidget {
	
    @Shadow
    protected boolean editable;
    
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
    
    @Overwrite(aliases = {"setEditable"})
    public void setIsEditable(boolean editable) {
    	if(this.editable != editable) {
    		this.editable = editable;
    		if(isTrulyFocused) {
    			updateIMState();
    		}
    	}
    }
}
