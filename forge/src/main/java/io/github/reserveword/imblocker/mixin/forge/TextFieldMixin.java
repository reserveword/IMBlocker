package io.github.reserveword.imblocker.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import io.github.reserveword.imblocker.common.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.common.IMCheckState;
import net.minecraft.client.gui.components.EditBox;

@Mixin(EditBox.class)
public abstract class TextFieldMixin implements FocusableWidgetAccessor {
	@Shadow
	private String f_94093_;
	
	@Shadow
	private boolean f_94098_;
	
	@Override
	public boolean isWidgetEditable() {
		return f_94098_;
	}
	
	@Override
	public String getText() {
		return f_94093_;
	}
	
    /*@Shadow
    public abstract boolean canConsumeInput();

    @Inject(method = {"tick", "renderWidget"}, at = @At("HEAD"))
    public void tickCallback(CallbackInfo ci) {
        IMCheckState.captureTick(this, this.canConsumeInput());
    }

    @Inject(method = "charTyped", at = @At("HEAD"))
    public void charTypedCallback(char codePoint, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        IMCheckState.captureNonPrintable(this, codePoint, this.canConsumeInput());
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    public void onClickCallback(double p_94125_, double p_94126_, int p_94127_, CallbackInfoReturnable<Boolean> cir) {
        IMCheckState.captureClick(this::canConsumeInput);
    }*/
	
	@Inject(method = "m_93692_", at = @At("TAIL"))
	public void focusChanged(boolean isFocused) {
		if(isFocused) {
    		IMCheckState.focusGained(this);
    	}else {
    		IMCheckState.focusLost(this);
    	}
	}
}
