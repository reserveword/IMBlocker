package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.IMCheckState;

@Pseudo
@Mixin(targets = {
        "me.shedaniel.rei.impl.client.gui.widget.basewidgets.TextFieldWidget",
        "me.shedaniel.rei.gui.widget.TextFieldWidget"
}, remap = false)
public abstract class ReiTextFieldMixin implements FocusableWidgetAccessor {
    @Shadow
    protected boolean editable;
    
    @Override
    public boolean isWidgetEditable() {
    	return editable;
    }
    
    @Inject(method = "setFocused", at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
    	if(isFocused) {
    		IMCheckState.focusGained(this);
    	}else {
    		IMCheckState.focusLost(this);
    	}
    }
}
