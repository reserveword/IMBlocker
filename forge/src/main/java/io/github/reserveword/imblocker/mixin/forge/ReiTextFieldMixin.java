package io.github.reserveword.imblocker.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;

@Pseudo
@Mixin(targets = {
        "me.shedaniel.rei.impl.client.gui.widget.basewidgets.TextFieldWidget",
        "me.shedaniel.rei.gui.widget.TextFieldWidget"})
public abstract class ReiTextFieldMixin implements MinecraftFocusableWidget {
	
	@Shadow
	private boolean editable;
	
	@Override
	public boolean isWidgetEditable() {
		return editable;
	}
	
	@Inject(method = {"setFocused", "m_93692_"}, at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onFocusChanged(isFocused);
	}
	
	@Inject(method = {"setEditable", "setIsEditable"}, at = @At("HEAD"))
    public void setIsEditable(boolean editable, CallbackInfo ci) {
		if(this.editable != editable) {
    		this.editable = editable;
    		if(isTrulyFocused()) {
    			updateIMState();
    		}
    	}
    }
}
