package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.supermartijn642.core.gui.widget.premade.TextFieldWidget;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.Point;

@Mixin(value = TextFieldWidget.class, remap = false)
public abstract class SM642TextFieldMixin extends SM642WidgetMixin {
	
	@Shadow private String text;
	@Shadow protected int lineScrollOffset;
	@Shadow protected int cursorPosition;
	
	@Override
	public boolean isWidgetEditable() {
		return true;
	}
	
	@Inject(method = "setSelected", at = @At("TAIL"))
	public void focusChanged(boolean selected, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(selected);
	}
	
	@Inject(method = "update", at = @At("TAIL"))
	public void onUpdate(CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public Point getCaretPos() {
		int caretX = 4 + MinecraftClientAccessor.INSTANCE.getStringWidth(
				StringUtil.getSubstring(text, lineScrollOffset, cursorPosition));
		return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, (height - 8) / 2);
	}
}
