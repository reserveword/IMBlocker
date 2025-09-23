package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import net.minecraft.client.gui.components.EditBox;

@Mixin(EditBox.class)
public abstract class TextFieldMixin extends AbstractWidgetMixin implements MinecraftTextFieldWidget {
	
	@Shadow
	private boolean isEditable;
	
	@Shadow private boolean bordered;
	@Shadow private int displayPos;
	@Shadow private int cursorPos;
	@Shadow private String value;

	private final SinglelineCursorInfo imblocker$cursorInfo = 
			new SinglelineCursorInfo(bordered, height, displayPos, cursorPos, value);

	private boolean preferredEnglishState = getPrimaryEnglishState();

	@Unique
	private boolean isRenderable = true;
	
	@Unique
	private long lastRenderTime;
	
	@Shadow
	public abstract boolean canConsumeInput();
	
	@Inject(method = "setFocused", at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		imblocker$onFocusChanged(canConsumeInput());
	}
	
	@Inject(method = "setVisible", at = @At("TAIL"))
	public void visibilityChanged(boolean isVisible, CallbackInfo ci) {
		imblocker$onFocusChanged(canConsumeInput());
	}
	
	@Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.isTrackingFocus) {
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
		imblocker$onCursorChanged();
	}
	
	@Override
	public boolean updateCursorInfo() {
		return imblocker$cursorInfo.updateCursorInfo(bordered, height, displayPos, cursorPos, value);
	}

	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return imblocker$cursorInfo;
	}
	
	@Override
	public void updateLastRenderTime(CallbackInfoReturnable<Boolean> ci) {
		if(FocusManager.isGameRendering) {
			lastRenderTime = System.nanoTime();
		}
	}

	@Overwrite
	public void setEditable(boolean editable) {
		if (this.isEditable != editable) {
			this.isEditable = editable;
			imblocker$onFocusChanged(canConsumeInput());
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
	public boolean getPreferredEnglishState() {
		return preferredEnglishState;
	}

	@Override
	public void checkVisibility(long lastGameRenderTime) {
		setRenderable(lastRenderTime > lastGameRenderTime);
	}
	
	@Unique
	private void setRenderable(boolean renderable) {
		if(isRenderable != renderable) {
			this.isRenderable = renderable;
			getFocusContainer().locateRealFocus();
		}
	}
	
	@Unique
	@Override
	public boolean isRenderable() {
		return isRenderable;
	}
}
