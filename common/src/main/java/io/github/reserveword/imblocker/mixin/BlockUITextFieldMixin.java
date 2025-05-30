package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.Point;

@Mixin(targets = "com.ldtteam.blockui.controls.TextField", remap = false)
public abstract class BlockUITextFieldMixin extends BlockUIPaneMixin {
	
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
	public Point getCaretPos() {
		int caretX = 4 + MinecraftClientAccessor.INSTANCE.getStringWidth(
				StringUtil.getSubstring(text, scrollOffset, cursorPosition));
		return new Point(getBOScreenFieldValue("renderScale"), caretX, (height - 8) / 2);
	}
}
