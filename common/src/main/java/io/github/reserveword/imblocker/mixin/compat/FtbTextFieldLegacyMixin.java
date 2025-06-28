package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.input.Key;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;

@Mixin(value = TextBox.class, remap = false)
public abstract class FtbTextFieldLegacyMixin extends FtbWidgetMixin implements MinecraftTextFieldWidget {
	
	@Shadow private String text;
	@Shadow private int lineScrollOffset;
	@Shadow private int cursorPosition;
	
	@Shadow
	private boolean isFocused;
	
	@Inject(method = "setFocused", at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(this.isFocused);
	}
	
	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(Key key, CallbackInfoReturnable<Boolean> cir) {
		if(IMBlockerCore.isTrackingFocus) {
			if(isFocused) {
				FocusContainer.MINECRAFT.switchFocus(this);
				cir.setReturnValue(true);
			}else {
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(method = "onClosed", at = @At("TAIL"))
	public void cancelFocus(CallbackInfo ci) {
		onMinecraftWidgetFocusLost();
	}

	@Inject(method = "setSelectionPos", at = @At("TAIL"))
	public void onCursorPosChanged(int position, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}

	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return new SinglelineCursorInfo(true, height, lineScrollOffset, cursorPosition, text);
	}
}
