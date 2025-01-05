package io.github.reserveword.imblocker.mixin.forge;

import io.github.reserveword.imblocker.common.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.common.IMCheckState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {
        "me.shedaniel.rei.impl.client.gui.widget.basewidgets.TextFieldWidget",
        "me.shedaniel.rei.gui.widget.TextFieldWidget"})
public abstract class ReiTextFieldMixin implements FocusableWidgetAccessor {
    @Shadow
    private boolean editable;

    @Override
    public boolean isWidgetEditable() {
        return editable;
    }

    @Inject(method = {"setFocused", "m_93692_"}, at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
        IMCheckState.focusChanged(this, isFocused);
    }
}
