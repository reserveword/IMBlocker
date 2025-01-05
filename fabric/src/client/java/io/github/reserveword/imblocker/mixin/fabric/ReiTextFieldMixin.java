package io.github.reserveword.imblocker.mixin.fabric;

import io.github.reserveword.imblocker.common.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.rules.FocusRule;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {
        "me.shedaniel.rei.impl.client.gui.widget.basewidgets.TextFieldWidget",
        "me.shedaniel.rei.gui.widget.TextFieldWidget"
}, remap = false)
public abstract class ReiTextFieldMixin implements FocusableWidgetAccessor {
    @Shadow
    protected boolean editable;

    @Override
    public boolean isWidgetEditable() {
        return editable;
    }

    @Inject(method = {"setFocused", "method_25365"}, at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
        FocusRule.focusChanged(this, isFocused);
    }
}
