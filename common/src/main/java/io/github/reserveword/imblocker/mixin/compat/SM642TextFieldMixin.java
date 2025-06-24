package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.supermartijn642.core.gui.widget.premade.TextFieldWidget;

import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;

@Mixin(value = TextFieldWidget.class, remap = false)
public abstract class SM642TextFieldMixin extends SM642WidgetMixin implements MinecraftTextFieldWidget {
	
	@Shadow private String text;
	@Shadow protected int lineScrollOffset;
	@Shadow protected int cursorPosition;
	
	@Shadow
	public abstract boolean canWrite();
	
	@Inject(method = "setSelected", at = @At("TAIL"))
	public void focusChanged(boolean selected, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(canWrite());
	}
	
	@Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(char character, boolean hasBeenHandled, CallbackInfoReturnable<Boolean> cir) {
		if(Common.isTrackingFocus && canWrite()) {
			FocusContainer.MINECRAFT.requestFocus(this);
			cir.setReturnValue(true);
		}
	}
	
	@Inject(method = "update", at = @At("TAIL"))
	public void onUpdate(CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return new SinglelineCursorInfo(true, height, lineScrollOffset, cursorPosition, text);
	}
}
