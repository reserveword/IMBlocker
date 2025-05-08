package io.github.reserveword.imblocker.mixin;

import java.lang.reflect.Method;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.IMBlockerFabric;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.components.SearchTextFieldComponent;
import net.minecraft.client.gui.Element;

@Pseudo
@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options.client.gui.frame.AbstractFrame", remap = false)
public abstract class RSOAbstractFrameMixin {
    @Shadow
    private Element focused;

    @Inject(method = "method_25395", at = @At("HEAD"))
    public void notifyFocusLost(@Nullable Element focused, CallbackInfo ci) {
        if (this.focused != null && this.focused != focused) {
        	if(IMBlockerFabric.isGameVersionReached(762/*1.19.4*/)) {
        		this.focused.setFocused(false);
        	}else if(this.focused.getClass().equals(SearchTextFieldComponent.class)) {
        		try {
					Method setFocusedMethod = SearchTextFieldComponent.class
							.getDeclaredMethod("setFocused", boolean.class);
					setFocusedMethod.setAccessible(true);
					setFocusedMethod.invoke(this.focused, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        }
    }
}
