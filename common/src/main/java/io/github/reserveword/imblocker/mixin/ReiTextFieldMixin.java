package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import me.shedaniel.rei.impl.client.gui.widget.basewidgets.TextFieldWidget;

@Pseudo
@Mixin(value = TextFieldWidget.class, remap = false)
public abstract class ReiTextFieldMixin implements MinecraftTextFieldWidget {
	
	@Shadow
	protected boolean editable;

	@Shadow
	private me.shedaniel.math.Rectangle bounds;

	@Shadow private boolean hasBorder;
	@Shadow protected int firstCharacterIndex;
	@Shadow protected int cursorPos;
	@Shadow private String text;

	@Inject(method = {"setFocused", "method_25365", "m_93692_"}, at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isFocused);
	}

	@Inject(method = "onChanged", at = @At("TAIL"))
	public void onTextChanged(String newText, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}

	@Inject(method = "moveCursorTo", at = @At("TAIL"))
	public void onMoveCursor(int cursor, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}

	@Overwrite(aliases = {"setEditable"})
	public void setIsEditable(boolean editable) {
		if(this.editable != editable) {
			this.editable = editable;
			if(isTrulyFocused()) {
				updateIMState();
			}
		}
	}

	@Override
	public boolean getPreferredState() {
		return editable;
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
