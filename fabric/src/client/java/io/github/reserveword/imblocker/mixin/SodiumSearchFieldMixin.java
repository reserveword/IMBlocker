package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.StringUtil;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

@Pseudo
@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options."
		+ "client.gui.frame.components.SearchTextFieldComponent", remap = false)
public abstract class SodiumSearchFieldMixin implements MinecraftFocusableWidget {
	
	@Shadow
	protected boolean editable;
	
	@Shadow protected String text;
	@Shadow private int firstCharacterIndex;
	@Shadow private int selectionStart;
	
	@Override
	public boolean isWidgetEditable() {
		return editable; // Always true.
	}
	
	@Inject(method = {"setFocused", "method_25365"}, at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isFocused);
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
		return new Rectangle(FocusContainer.getMCGuiScaleFactor(), x, y, width, height);
	}
	
	@Override
	public Point getCaretPos() {
		TextRenderer font = MinecraftClient.getInstance().textRenderer;
		int caretX = 6 + font.getWidth(StringUtil.getSubstring(text, firstCharacterIndex, selectionStart));
		return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, 5);
	}
}
