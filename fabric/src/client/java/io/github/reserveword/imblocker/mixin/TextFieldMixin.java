package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldMixin extends ClickableWidgetMixin implements MinecraftTextFieldWidget {
	
	@Shadow
	private boolean editable;
	
	@Shadow private boolean drawsBackground;
	@Shadow private int firstCharacterIndex;
	@Shadow private int selectionStart;
	@Shadow private String text;
	
	private boolean preferredEditState = true;
	private boolean preferredEnglishState = false;
	
	@Unique
	private boolean isRenderable = true;
	
	@Unique
	private long lastRenderTime;
	
	@Shadow
	public abstract boolean isActive();

	@Inject(method = {"setFocused", "method_25365"}, at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		imblocker$onFocusChanged(isActive());
	}
	
	@Inject(method = "setVisible", at = @At("TAIL"))
	public void visibilityChanged(boolean isVisible, CallbackInfo ci) {
		imblocker$onFocusChanged(isActive());
	}
	
	@Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.isTrackingFocus) {
			if(isActive()) {
				getFocusContainer().switchFocus(this);
				cir.setReturnValue(true);
			}else {
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(method = "onChanged", at = @At("TAIL"))
	public void onTextChanged(String newText, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Inject(method = "isVisible", at = @At("TAIL"))
	public void updateLastRenderTime(CallbackInfoReturnable<Boolean> ci) {
		if(FocusManager.isGameRendering) {
			lastRenderTime = System.nanoTime();
		}
	}

	@Overwrite
	public void setEditable(boolean editable) {
		if (this.editable != editable) {
			this.editable = editable;
			imblocker$onFocusChanged(isActive());
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
