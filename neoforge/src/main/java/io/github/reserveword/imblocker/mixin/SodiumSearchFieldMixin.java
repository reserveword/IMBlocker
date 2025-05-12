package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Point;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import net.caffeinemc.mods.sodium.client.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

@Pseudo
@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options."
		+ "client.gui.frame.components.SearchTextFieldComponent", remap = false)
public abstract class SodiumSearchFieldMixin implements MinecraftFocusableWidget {
	
	@Shadow
	protected boolean editable;
	
	@Shadow protected Dim2i dim;
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
		return new Rectangle(FocusContainer.getMCGuiScaleFactor(), dim.x(), dim.y(), dim.width(), dim.height());
	}
	
	@Override
	public Point getCaretPos() {
		Font font = Minecraft.getInstance().font;
		int caretX = 6;
		try {
			caretX += font.width(text.substring(firstCharacterIndex, selectionStart));
		} catch (Exception e) {}
		return new Point(FocusContainer.getMCGuiScaleFactor(), caretX, 5);
	}
}
