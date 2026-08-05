package io.github.reserveword.imblocker.mixin.compat;

import java.util.List;

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
import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.accessor.MinecraftClientAccessor;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;

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
		IMManager.updateCompositionWindowPos();
	}
	
	@Inject(method = { "onHorizontalScroll", "onVerticalScroll" }, at = @At("TAIL"))
	public void onScrolling(float value, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public void imblocker$onFocusFactorsChanged() {
		imblocker$onFocusChanged(isEditable());
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(getGuiScale(), 
				(int) contentView.getContentX(), (int) contentView.getContentY(), 
				(int) contentView.getContentWidth(), (int) contentView.getContentHeight());
	}
	
	@Override
	public Point getCaretPos() {
		float fontSize = textAreaStyle.fontSize();
		int caretX = (int) (MinecraftClientAccessor.INSTANCE.getStringWidth(
				imblocker$getDedicatedFontRenderer(), StringUtil.getSubstring(lines.get(cursorLine), 0, cursorCol), imblocker$getFont()
				) * fontSize / 9.0F - scrollX);
		int caretY = (int) (cursorLine * (fontSize + textAreaStyle.lineSpacing()) - scrollY);
		return new Point(getGuiScale(), caretX, caretY);
	}
	
	private Object imblocker$getFont() {
		return ReflectionUtil.invokeMethod(TextAreaStyle.class, textAreaStyle, null, "font", new Class[0]);
	}
	
	@Override
	public int getFontHeight() {
		return (int) textAreaStyle.fontSize();
	}
}
