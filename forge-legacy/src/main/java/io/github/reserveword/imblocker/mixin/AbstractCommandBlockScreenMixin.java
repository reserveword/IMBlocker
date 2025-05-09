package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.ChatCommandInputType;
import io.github.reserveword.imblocker.common.Config;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import net.minecraft.client.gui.screen.AbstractCommandBlockScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(AbstractCommandBlockScreen.class)
public class AbstractCommandBlockScreenMixin {
	
	@Shadow
	protected TextFieldWidget commandEdit;

	@Inject(method = "init", at = @At("TAIL"))
	private void setCommandInputEnglishState(CallbackInfo ci) {
		if(Config.INSTANCE.getChatCommandInputType() == ChatCommandInputType.DISABLE_IM) {
			((MinecraftFocusableWidget) commandEdit).setPreferredEditState(false); 
		}
		((MinecraftFocusableWidget) commandEdit).setPreferredEnglishState(true); 
	}
}
