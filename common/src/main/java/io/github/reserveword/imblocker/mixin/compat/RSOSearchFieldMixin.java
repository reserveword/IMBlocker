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
import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Pseudo
@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options"
		+ ".client.gui.frame.components.SearchTextFieldComponent", remap = false)
public abstract class RSOSearchFieldMixin implements MinecraftTextFieldWidget {
	
	@Shadow protected String text;
	@Shadow private int firstCharacterIndex;
	@Shadow private int selectionStart;
	
	@Shadow(aliases = {"method_37303", "m_142518_"})
	public abstract boolean isActive();
	
	@Inject(method = {"setFocused", "method_25365", "m_93692_"}, at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isActive());
	}
	
	@Inject(method = {"keyPressed", "method_25404", "m_7933_", "func_231046_a_"}, at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if(IMBlockerCore.isTrackingFocus) {
			if(isActive()) {
				FocusContainer.MINECRAFT.switchFocus(this);
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
	
	@Override
	public Rectangle getBoundsAbs() {
		Object dim = ReflectionUtil.getFieldValue(getClass(), this, Object.class, "dim");
		int x = ReflectionUtil.getFieldValue(dim.getClass(), dim, int.class, "x");
		int y = ReflectionUtil.getFieldValue(dim.getClass(), dim, int.class, "y");
		int width = ReflectionUtil.getFieldValue(dim.getClass(), dim, int.class, "width");
		int height = ReflectionUtil.getFieldValue(dim.getClass(), dim, int.class, "height");
		return new Rectangle(getGuiScale(), x, y, width, height);
	}
	
	@Override
	public SinglelineCursorInfo getCursorInfo() {
		Object dim = ReflectionUtil.getFieldValue(getClass(), this, Object.class, "dim");
		int height = ReflectionUtil.getFieldValue(dim.getClass(), dim, int.class, "height");
		return new SinglelineCursorInfo(true, height, firstCharacterIndex, selectionStart, text);
	}
	
	@Override
	public int getPaddingX() {
		return 6;
	}
}
