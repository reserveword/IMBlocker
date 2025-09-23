package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;

@Mixin(AbstractCommandBlockEditScreen.class)
public abstract class AbstractCommandBlockScreenMixin {
	
	@Shadow
	protected EditBox commandEdit;

	@Inject(method = "init", at = @At("TAIL"))
	private void setCommandInputEnglishState(CallbackInfo ci) {
		((MinecraftTextFieldWidget) commandEdit).setPreferredEnglishState(true); 
	}
}
