package io.github.reserveword.imblocker.mixin.fabric;

import io.github.reserveword.imblocker.common.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.common.IMCheckState;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldMixin implements FocusableWidgetAccessor {
    @Shadow
    private String text;

    @Shadow
    private boolean editable;

    @Override
    public boolean isWidgetEditable() {
        return editable;
    }

    @Override
    public String getText() {
        return text;
    }

    @Inject(method = {"setFocused", "method_25365"}, at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
        IMCheckState.focusChanged(this, isFocused);
    }
}
