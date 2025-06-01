package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.CursorInfo;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;

@Mixin(targets = "com.ldtteam.blockui.controls.TextField", remap = false)
public abstract class BlockUITextFieldMixin extends BlockUIPaneMixin implements MinecraftTextFieldWidget {
	
	@Shadow protected String text;
	@Shadow protected int scrollOffset = 0;
	@Shadow protected int cursorPosition = 0;
	
	@Override
	public boolean isWidgetEditable() {
		return true;
	}
	
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
	}
	
	@Override
	public CursorInfo getCursorInfo() {
		return new CursorInfo(true, height, 0, 0, scrollOffset, cursorPosition, text);
	}
}
