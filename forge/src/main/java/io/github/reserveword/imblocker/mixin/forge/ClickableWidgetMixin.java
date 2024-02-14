package io.github.reserveword.imblocker.mixin.forge;

import io.github.reserveword.imblocker.Common;
import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractWidget.class)
public abstract class ClickableWidgetMixin {
    @Shadow public abstract boolean isFocused();

    @Inject(method = "render", at=@At("HEAD"))
    public void captureTick(GuiGraphics p_282421_, int p_93658_, int p_93659_, float p_93660_, CallbackInfo ci) {
        if (Common.classIsTextField(this.getClass())) {
            IMCheckState.captureTick(this, isFocused());
        }
    }
}
