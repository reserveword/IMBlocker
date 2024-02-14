package io.github.reserveword.imblocker.mixin.fabric;

import io.github.reserveword.imblocker.Common;
import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClickableWidget.class)
public abstract class ClickableWidgetMixin {
    @Shadow public abstract boolean isFocused();

    @Inject(method = "render", at=@At("HEAD"))
    public void captureTick(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (Common.classIsTextField(this.getClass())) {
            IMCheckState.captureTick(this, isFocused());
        }
    }
}
