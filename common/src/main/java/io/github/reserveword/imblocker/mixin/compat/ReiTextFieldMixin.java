package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
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
	
	private final SinglelineCursorInfo imblocker$cursorInfo = 
			new SinglelineCursorInfo(hasBorder, 0, firstCharacterIndex, cursorPos, text);

	@Inject(method = {"setFocused", "method_25365", "m_93692_"}, at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		imblocker$onFocusChanged(visible && focused);
	}
	
	@Inject(method = {"charTyped", "method_25400", "m_5534_", "func_231042_a_"}, at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.isTrackingFocus) {
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
		imblocker$onFocusChanged(this.visible && focused);
	}

	@Inject(method = "onChanged", at = @At("TAIL"))
	public void onTextChanged(String newText, CallbackInfo ci) {
		imblocker$onCursorChanged();
	}

	@Inject(method = "moveCursorTo", at = @At("TAIL"))
	public void onMoveCursor(int cursor, CallbackInfo ci) {
		imblocker$onCursorChanged();
	}
	
	@Override
	public boolean updateCursorInfo() {
		return imblocker$cursorInfo.updateCursorInfo(hasBorder, bounds.height, firstCharacterIndex, cursorPos, text);
	}

	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return imblocker$cursorInfo;
	}
	
	@Override
	public boolean getPreferredState() {
		return getPrimaryEditState();
	}
	
	@Override
	public boolean getPreferredEnglishState() {
		return getPrimaryEnglishState();
	}

	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(getGuiScale(), bounds.x, bounds.y, bounds.width, bounds.height);
	}
}
