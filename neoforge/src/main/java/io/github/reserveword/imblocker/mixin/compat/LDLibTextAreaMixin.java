package io.github.reserveword.imblocker.mixin.compat;

import java.util.List;

import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextArea;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextArea.TextAreaStyle;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvent;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.Point;

@Mixin(value = TextArea.class, remap = false)
public abstract class LDLibTextAreaMixin extends LDLibUIElementMixin {
	
	@Shadow
	private UIElement contentView;
	
	@Shadow
	private TextAreaStyle textAreaStyle;
	
	@Shadow protected List<String> lines;
	@Shadow private int cursorLine;
	@Shadow private int cursorCol;
	@Shadow private float scrollX;
	@Shadow private float scrollY;
	
	@Shadow public abstract boolean isEditable();
	
	@Inject(method = "onCharTyped", at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(UIEvent event, CallbackInfo ci) {
		if(FocusManager.isTrackingFocus) {
			if(isEditable()) {
				FocusContainer.MINECRAFT.switchFocus(this);
			}
			ci.cancel();
		}
	}
	
	@Inject(method = "updateScrollers", at = @At("TAIL"))
	public void onScrollersUpdated(CallbackInfo ci) {
		IMManager.updateCaretPosition();
	}
	
	@Inject(method = { "onHorizontalScroll", "onVerticalScroll" }, at = @At("TAIL"))
	public void onScrolling(float value, CallbackInfo ci) {
		IMManager.updateCaretPosition();
	}
	
	@Override
	public void imblocker$onFocusFactorsChanged() {
		imblocker$onFocusChanged(isEditable());
	}
	
	@Override
	protected UIElement imblocker$getAnchorWidget() {
		return contentView;
	}
	
	@Override
	public Point getCaretPos() {
		float fontSize = textAreaStyle.fontSize();
		float caretX = imblocker$getStringWidth(StringUtil.getSubstring(
				lines.get(cursorLine), 0, cursorCol), textAreaStyle.font()) * fontSize / 9.0F - scrollX;
		float caretY = cursorLine * (fontSize + textAreaStyle.lineSpacing()) - scrollY;
		if(!imblocker$isPoseIdentity()) {
			Matrix3x2f t = imblocker$currentPose;
			caretX = t.m00 * caretX;
			caretY = t.m11 * caretY;
		}
		return new Point(getGuiScale(), caretX, caretY);
	}
	
	@Override
	public int getFontHeight() {
		return (int) (textAreaStyle.fontSize() * imblocker$currentPose.m00);
	}
}
