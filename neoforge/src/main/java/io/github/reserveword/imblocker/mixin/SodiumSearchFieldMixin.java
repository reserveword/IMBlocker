package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import net.caffeinemc.mods.sodium.client.util.Dim2i;

@Pseudo
@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options."
		+ "client.gui.frame.components.SearchTextFieldComponent", remap = false)
public abstract class SodiumSearchFieldMixin implements MinecraftTextFieldWidget {
	
	@Shadow
	protected boolean editable;
	
	@Shadow protected Dim2i dim;
	@Shadow protected String text;
	@Shadow private int firstCharacterIndex;
	@Shadow private int selectionStart;
	
	@Inject(method = {"setFocused", "method_25365"}, at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		onMinecraftWidgetFocusChanged(isFocused);
	}
	
	@Inject(method = "onChanged", at = @At("TAIL"))
	public void onTextChanged(String newText, CallbackInfo ci) {
		IMManager.updateCompositionWindowPos();
	}
	
	@Override
	public boolean getPreferredState() {
		return editable;
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(getGuiScale(), dim.x(), dim.y(), dim.width(), dim.height());
	}
	
	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return new SinglelineCursorInfo(true, dim.height(), firstCharacterIndex, selectionStart, text);
	}
	
	@Override
	public int getPaddingX() {
		return 6;
	}
}
