package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.common.IMCheckState;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldMixin implements FocusableWidgetAccessor {
	@Shadow
	private String text;
	
	@Shadow
	private boolean editable;
	
    @Shadow
    public abstract boolean isActive();
    
    @Override
    public boolean isWidgetEditable() {
    	return editable;
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
    
    @Inject(method = {"setFocused", "method_25365"}, at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
    	IMCheckState.focusChanged(this, isFocused);
    }
}
