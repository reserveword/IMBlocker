package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.CommandInputMode;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(AbstractCommandBlockScreen.class)
public class AbstractCommandBlockScreenMixin {
	
	@Shadow
	protected TextFieldWidget consoleCommandTextField;
	
	@Inject(method = "init", at = @At("TAIL"))
	private void setCommandInputEnglishState(CallbackInfo ci) {
		if(IMBlockerConfig.INSTANCE.getChatCommandInputType() == CommandInputMode.DISABLE_IM) {
			((MinecraftFocusableWidget) consoleCommandTextField).setPreferredEditState(false); 
		}
		((MinecraftFocusableWidget) consoleCommandTextField).setPreferredEnglishState(true); 
	}
}
