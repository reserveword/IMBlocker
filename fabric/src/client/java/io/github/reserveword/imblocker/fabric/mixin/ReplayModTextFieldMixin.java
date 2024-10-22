package io.github.reserveword.imblocker.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.common.IMCheckState;

@Pseudo
@Mixin(targets = {
        "com.replaymod.lib.de.johni0702.minecraft.gui.element.AbstractGuiTextField"
}, remap = false)
public abstract class ReplayModTextFieldMixin implements FocusableWidgetAccessor {
    @Shadow
    public abstract boolean isFocused();
    
    @Override
    public boolean isWidgetEditable() {
    	return true;
    }

    /*
    @Inject(method = "writeChar*", at = @At("HEAD"))
    public void charTypedCallback(char c, CallbackInfoReturnable<Object> cir) {
        IMCheckState.captureNonPrintable(this, c, this.isFocused());
    }
    */
    
    @Inject(method = "onFocusChanged", at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
    	if(isFocused) {
    		IMCheckState.focusGained(this);
    	}else {
    		IMCheckState.focusLost(this);
    	}
    }
}
