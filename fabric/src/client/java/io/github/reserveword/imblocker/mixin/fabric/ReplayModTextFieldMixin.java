package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;

@Pseudo
@Mixin(targets = {
        "com.replaymod.lib.de.johni0702.minecraft.gui.element.AbstractGuiTextField"
}, remap = false)
public abstract class ReplayModTextFieldMixin implements MinecraftFocusableWidget {
    
    @Override
    public boolean isWidgetEditable() {
    	return true;
    }
    
    @Inject(method = "onFocusChanged", at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
    	MinecraftFocusableWidget.super.onFocusChanged(isFocused);
    }
}
