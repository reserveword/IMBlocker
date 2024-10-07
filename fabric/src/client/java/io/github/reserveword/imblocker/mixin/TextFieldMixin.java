package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldMixin implements FocusableWidgetAccessor {
	@Shadow
	private String text;
	
    @Shadow
    public abstract boolean isActive();
    
    @Shadow
    public abstract boolean isEditable();
    
    @Override
    public boolean isWidgetEditable() {
    	return isEditable();
    }

    @Override
    public String getText() {
    	return text;
    }
    
    /*
    @Inject(method = "tick", at = @At("HEAD"))
    public void tickCallback(CallbackInfo ci) {
        IMCheckState.captureTick(this, this.isActive());
    }

    @Inject(method = "charTyped", at = @At("HEAD"))
    public void charTypedCallback(char codePoint, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        IMCheckState.captureNonPrintable(this, codePoint, this.isActive());
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    public void onClickCallback(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
        IMCheckState.captureClick(this::isActive);
    }
    */
    
    @Inject(method = "setFocused", at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
    	if(isFocused) {
    		IMCheckState.focusGained(this);
    	}else {
    		IMCheckState.focusLost(this);
    	}
    }
}
