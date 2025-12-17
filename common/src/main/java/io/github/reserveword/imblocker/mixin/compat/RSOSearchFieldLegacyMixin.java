package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.gui.FocusContainer;
import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;
import io.github.reserveword.imblocker.common.gui.SinglelineCursorInfo;
import net.caffeinemc.mods.sodium.client.util.Dim2i;
import net.minecraft.client.input.CharacterEvent;

@Pseudo
@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options"
		+ ".client.gui.frame.components.SearchTextFieldComponent", remap = false)
public abstract class RSOSearchFieldLegacyMixin implements MinecraftTextFieldWidget {
	
	@Shadow 
	protected Dim2i dim;
	
	@Shadow protected String text;
	@Shadow private int firstCharacterIndex;
	@Shadow private int selectionStart;
	
	private final SinglelineCursorInfo imblocker$cursorInfo = 
			new SinglelineCursorInfo(true, 0, firstCharacterIndex, selectionStart, text);
	
	@Shadow(aliases = "method_37303")
	public abstract boolean isActive();
	
	@Inject(method = {"setFocused", "method_25365"}, at = @At("TAIL"))
	public void focusChanged(boolean isFocused, CallbackInfo ci) {
		imblocker$onFocusChanged(isActive());
	}
	
	@Inject(method = {"charTyped", "method_25400"}, at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(CharacterEvent characterEvent, CallbackInfoReturnable<Boolean> cir) {
		if(FocusManager.isTrackingFocus) {
			if(isActive()) {
				FocusContainer.MINECRAFT.switchFocus(this);
				cir.setReturnValue(true);
			}else {
				cir.setReturnValue(false);
			}
		}
	}
	
	@Inject(method = "onChanged", at = @At("TAIL"))
	public void onTextChanged(String newText, CallbackInfo ci) {
		imblocker$onCursorChanged();
	}
	
	@Override
	public boolean updateCursorInfo() {
		return imblocker$cursorInfo.updateCursorInfo(true, dim.height(), firstCharacterIndex, selectionStart, text);
	}
	
	@Override
	public SinglelineCursorInfo getCursorInfo() {
		return imblocker$cursorInfo;
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		return new Rectangle(getGuiScale(), dim.x(), dim.y(), dim.width(), dim.height());
	}
	
	@Override
	public int getPaddingX() {
		return 6;
	}
}
