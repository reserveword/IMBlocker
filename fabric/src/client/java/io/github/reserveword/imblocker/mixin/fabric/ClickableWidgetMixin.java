package io.github.reserveword.imblocker.mixin.fabric;

import io.github.reserveword.imblocker.Common;
import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClickableWidget.class)
public abstract class ClickableWidgetMixin {
    @Shadow public abstract boolean isFocused();
    @Shadow public abstract boolean isNarratable();

    @Inject(method = "render", at=@At("HEAD"))
    public void captureTick(MatrixStack context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        //noinspection ConstantValue
        if (!((Object)this instanceof TextFieldWidget) && Common.classIsTextField(this.getClass())) {
            IMCheckState.captureTick(this, isFocused() && isNarratable());
        }
    }
}
