package io.github.reserveword.imblocker.mixin.forge;

import io.github.reserveword.imblocker.common.FocusableWidgetAccessor;
import io.github.reserveword.imblocker.rules.FocusRule;

import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditBox.class)
public abstract class TextFieldMixin implements FocusableWidgetAccessor {
    @Shadow
    private String value;

    @Shadow
    private boolean isEditable;

    @Override
    public boolean isWidgetEditable() {
        return isEditable;
    }

    @Override
    public String getText() {
        return value;
    }

    @Inject(method = "setFocused", at = @At("TAIL"))
    public void focusChanged(boolean isFocused, CallbackInfo ci) {
        FocusRule.focusChanged(this, isFocused);
    }
}
