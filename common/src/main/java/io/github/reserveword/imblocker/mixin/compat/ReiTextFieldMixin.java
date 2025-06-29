package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import me.shedaniel.rei.impl.client.gui.widget.basewidgets.TextFieldWidget;

@Pseudo
@Mixin(value = TextFieldWidget.class, remap = false)
public abstract class ReiTextFieldMixin implements MinecraftTextFieldWidget {
	
	@Shadow
	private boolean visible;
	
	@Shadow
	private boolean focused;

	@Shadow
	private me.shedaniel.math.Rectangle bounds;

	@Shadow private boolean hasBorder;
	@Shadow protected int firstCharacterIndex;
	@Shadow protected int cursorPos;
	@Shadow private String text;

	@Inject(method = {"setFocused", "method_25365", "m_93692_"}, at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(visible && focused);
	}
	
	@Inject(method = {"charTyped", "method_25400", "m_5534_", "func_231042_a_"}, at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if(IMBlockerCore.isTrackingFocus) {
			if(visible && focused) {
				FocusContainer.MINECRAFT.switchFocus(this);
				cir.setReturnValue(true);
			}else {
				cir.setReturnValue(false);
			}
		}
	}
	
	@Inject(method = "setVisible", at = @At("TAIL"))
	public void visibilityChanged(boolean visible, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(this.visible && focused);
	}

	@Inject(method = "onChanged", at = @At("TAIL"))
	public void onTextChanged(String newText, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}

	@Inject(method = "moveCursorTo", at = @At("TAIL"))
	public void onMoveCursor(int cursor, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}

	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(getGuiScale(), bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return new SinglelineCursorInfo(hasBorder, bounds.height, firstCharacterIndex, cursorPos, text);
	}
}
