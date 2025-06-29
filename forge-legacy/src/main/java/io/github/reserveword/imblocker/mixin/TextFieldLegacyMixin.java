package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldLegacyMixin extends AbstractWidgetMixin implements MinecraftTextFieldWidget {
	
	@Shadow
	private boolean isEditable;
	
	@Shadow private boolean bordered;
	@Shadow private int displayPos;
	@Shadow private int cursorPos;
	@Shadow private String value;

	private boolean preferredEditState = true;

	private boolean preferredEnglishState = false;
	
	@Shadow
	public abstract boolean canConsumeInput();
	
	@Override
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(canConsumeInput());
	}
	
	@Inject(method = "onFocusedChanged", at = @At("TAIL"))
    public void focusBeChanged(boolean isFocused, CallbackInfo ci) {
    	onMinecraftWidgetFocusChanged(canConsumeInput());
    }
	
	@Inject(method = "setVisible", at = @At("TAIL"))
	public void visibilityChanged(boolean isVisible, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(canConsumeInput());
	}
	
	@Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if(IMBlockerCore.isTrackingFocus) {
			if(canConsumeInput()) {
				FocusContainer.MINECRAFT.switchFocus(this);
				cir.setReturnValue(true);
			}else {
				cir.setReturnValue(false);
			}
		}
	}
	
	@Inject(method = "onValueChange", at = @At("TAIL"))
    public void onTextChanged(String newValue, CallbackInfo ci) {
    	IMManager.updateCompositionWindowPos();
    }
	
	@Overwrite
    public void setEditable(boolean editable) {
		if(this.isEditable != editable) {
    		this.isEditable = editable;
    		onMinecraftWidgetFocusChanged(canConsumeInput());
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
    public boolean getPreferredState() {
    	return preferredEditState;
    }
    
    @Override
    public boolean getPreferredEnglishState() {
    	return preferredEnglishState;
    }
    
    @Override
    public SinglelineCursorInfo getCursorInfo() {
    	return new SinglelineCursorInfo(bordered, height, displayPos, cursorPos, value);
    }
}
