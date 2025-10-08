package io.github.reserveword.imblocker.mixin.compat;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.components.events.GuiEventListener;

@Pseudo
@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options.client.gui.frame.AbstractFrame", remap = false)
public abstract class RSOAbstractFrameMixin {
	
	@Shadow
	private GuiEventListener focused;

	@Inject(method = {"setFocused", "method_25395"}, at = @At("HEAD"), cancellable = true)
	public void filterInvalidFocusRequest(@Nullable GuiEventListener focused, CallbackInfo ci) {
		if (this.focused == focused) {
			ci.cancel();
		}
	}
}
