package io.github.reserveword.imblocker.mixin;

import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.Widget;
import io.github.reserveword.imblocker.IMCheckState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Widget.class, remap = false)
public abstract class FtbWidgetMixin {
	/*
    @Inject(method = "tick", at = @At("HEAD"))
    public void tickCallback(CallbackInfo ci) {
        if ((Object)(this) instanceof TextBox)
            IMCheckState.captureTick(this, ((TextBox)(Object)(this)).isFocused());
    }
    */
}
