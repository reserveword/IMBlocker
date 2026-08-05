package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.lowdragmc.lowdraglib2.gui.ui.elements.TextField;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextField.TextFieldStyle;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvent;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.Point;

@Mixin(value = TextField.class, remap = false)
public abstract class LDLibTextFieldMixin extends LDLibUIElementMixin {
	
	@Shadow
	private TextFieldStyle textFieldStyle;
	
	@Shadow private int cursorPos;
	@Shadow private String rawText;
	@Shadow private float displayOffset;
	
	@Shadow
	public abstract boolean isEditable();
	
	@Inject(method = "onCharTyped", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(UIEvent event, CallbackInfo ci) {
		if(FocusManager.isTrackingFocus) {
			if(isEditable()) {
				FocusContainer.MINECRAFT.switchFocus(this);
			}
			ci.cancel();
		}
	}
	
	@Inject(method = "updateDisplayOffset", at = @At("TAIL"))
	public void onDisplayOffsetUpdated(CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public void imblocker$onFocusFactorsChanged() {
		imblocker$onFocusChanged(isEditable());
	}
	
	@Override
	public Point getCaretPos() {
		float fontSize = textFieldStyle.fontSize();
		int caretX = (int) (MinecraftClientAccessor.INSTANCE.getStringWidth(
				imblocker$getDedicatedFontRenderer(), StringUtil.getSubstring(rawText, 0, cursorPos), imblocker$getFont()
				) * fontSize / 9.0F - displayOffset);
		int caretY = (int) ((getContentHeight() - fontSize) / 2);
		return new Point(getGuiScale(), caretX, caretY);
	}
	
	private Object imblocker$getFont() {
		return ReflectionUtil.invokeMethod(TextFieldStyle.class, textFieldStyle, null, "font", new Class[0]);
	}
	
	@Override
	public int getFontHeight() {
		return (int) textFieldStyle.fontSize();
	}
}
