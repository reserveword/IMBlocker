package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ldtteam.blockui.controls.TextField;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;

@Mixin(value = TextField.class, remap = false)
public abstract class BlockUITextFieldMixin extends BlockUIPaneMixin implements MinecraftTextFieldWidget {
	
	@Shadow protected String text;
	@Shadow protected int scrollOffset = 0;
	@Shadow protected int cursorPosition = 0;
	
	@Inject(method = "onFocus", at = @At("TAIL"))
	public void focusGained(CallbackInfo ci) {
		onMinecraftWidgetFocusGained();
	}
	
	@Override
	public void focusLost(CallbackInfo ci) {
		onMinecraftWidgetFocusLost();
	}
	
	@Inject(method = "onUpdate", at = @At("TAIL"))
	public void onUpdate(CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
		IMManager.updateCompositionFontSize();
	}
	
	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return new SinglelineCursorInfo(true, height, scrollOffset, cursorPosition, text);
	}
}
