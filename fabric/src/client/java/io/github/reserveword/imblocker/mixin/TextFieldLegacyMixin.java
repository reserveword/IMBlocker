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
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldLegacyMixin extends ClickableWidgetMixin implements MinecraftTextFieldWidget {
	
	@Shadow
	private boolean editable;
	
	@Shadow private boolean drawsBackground;
	@Shadow private int firstCharacterIndex;
	@Shadow private int selectionStart;
	@Shadow private String text;
	
	private boolean preferredEditState = true;
	
	private boolean preferredEnglishState = false;
	
	@Shadow
	public abstract boolean isActive();

	@Override
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isActive());
	}

	@Inject(method = "method_25363", at = @At("TAIL"))
	public void focusBeChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isActive());
	}
	
	@Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if(IMBlockerCore.isTrackingFocus && isActive()) {
			FocusContainer.MINECRAFT.requestFocus(this);
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "onChanged", at = @At("TAIL"))
	public void onTextChanged(String newText, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}

	@Overwrite
	public void setEditable(boolean editable) {
		if (this.editable != editable) {
			this.editable = editable;
			onMinecraftWidgetFocusChanged(isActive());
		}
	}

	@Override
	public void setPreferredEditState(boolean preferredEditState) {
		if (this.preferredEditState != preferredEditState) {
			this.preferredEditState = preferredEditState;
			if (isTrulyFocused()) {
				updateEnglishState();
			}
		}
	}

	@Override
	public void setPreferredEnglishState(boolean state) {
		if (preferredEnglishState != state) {
			preferredEnglishState = state;
			if (isTrulyFocused()) {
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
		return new SinglelineCursorInfo(drawsBackground, height, firstCharacterIndex, selectionStart, text);
	}
}
