package io.github.reserveword.imblocker.mixin.fabric;

import io.github.reserveword.imblocker.common.IMCheckState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method="render", at = @At("HEAD"))
    public void checkRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        IMCheckState.scheduleTickCheck();
    }
}
