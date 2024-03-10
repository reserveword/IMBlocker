package io.github.reserveword.imblocker.mixin.fabric;

import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method="render", at = @At("HEAD"))
    public void checkRender(MatrixStack context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        IMCheckState.scheduleTickCheck();
    }
}
