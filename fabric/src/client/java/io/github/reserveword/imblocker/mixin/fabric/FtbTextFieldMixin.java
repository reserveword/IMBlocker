package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftblibrary.ui.TextBox;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;

@Mixin(value = TextBox.class, remap = false)
public abstract class FtbTextFieldMixin implements MinecraftFocusableWidget {
    
    @Override
    public boolean isWidgetEditable() {
    	return true;
    }
    
    @Inject(method = "setFocused", at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
    	onFocusChanged(isFocused);
    }
}
