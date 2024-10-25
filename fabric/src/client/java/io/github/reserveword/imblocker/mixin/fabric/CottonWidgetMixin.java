package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.reserveword.imblocker.common.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.common.IMCheckState;

@Pseudo
@Mixin(value = WWidget.class, remap = false)
public abstract class CottonWidgetMixin implements FocusableWidgetAccessor {
    
    @Override
    public boolean isWidgetEditable() {
    	return (getClass().equals(WTextField.class)) && ((CottonTextFieldMixin) this).isEditable();
    }

    /*
    @Inject(method = "tick", at = @At("HEAD"))
    public void tickCallback(CallbackInfo ci) {
        IMCheckState.captureTick(this, this.editable);
    }

    @Inject(method = "onCharTyped", at = @At("HEAD"), cancellable = true)
    public void charTypedCallback(char ch, CallbackInfoReturnable<InputResult> cir) {
        if (IMCheckState.captureNonPrintable(this, ch, this.editable)) {
            cir.setReturnValue(InputResult.IGNORED);
        }
    }
    */
    
    @Inject(method = "onFocusGained", at = @At("HEAD"))
    public void onFocusGained(CallbackInfo ci) {
    	IMCheckState.focusGained(this);
    }
    
    @Inject(method = "onFocusLost", at = @At("HEAD"))
    public void onFocusLost(CallbackInfo ci) {
    	IMCheckState.focusLost(this);
    }
}
