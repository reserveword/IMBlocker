package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
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

	@Override
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isFocused);
	}

	@Inject(method = "method_25363", at = @At("TAIL"))
	public void focusBeChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isFocused);
	}

	@Inject(method = "onChanged", at = @At("TAIL"))
	public void onTextChanged(String newText, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}

	@Overwrite
	public void setEditable(boolean editable) {
		if (this.editable != editable) {
			this.editable = editable;
			if (isTrulyFocused()) {
				updateIMState();
			}
		}
	}

	@Override
	public void setPreferredEditState(boolean preferredEditState) {
		if (this.preferredEditState != preferredEditState) {
			this.preferredEditState = preferredEditState;
			if (isTrulyFocused()) {
				updateIMState();
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
		return editable && preferredEditState;
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
