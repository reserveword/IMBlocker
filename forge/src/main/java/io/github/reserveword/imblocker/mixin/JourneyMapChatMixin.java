package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.MinecraftTextFieldWidget;

@Pseudo
@Mixin(targets = "journeymap.client.ui.fullscreen.MapChat", remap = false)
public abstract class JourneyMapChatMixin extends ChatScreenMixin {

	@Shadow
	protected boolean hidden;
	
	@Override
	protected void initChatState(CallbackInfo ci) {
		super.initChatState(ci);
		((MinecraftTextFieldWidget) input).setPreferredEditState(!this.hidden);
	}
	
	@Inject(method = "close", at = @At("TAIL"))
	public void updateVisible(CallbackInfo ci) {
		((MinecraftTextFieldWidget) input).setPreferredEditState(false);
	}
	
	@Inject(method = "setHidden", at = @At("TAIL"))
	public void updateVisible(boolean hidden, CallbackInfo ci) {
		((MinecraftTextFieldWidget) input).setPreferredEditState(!this.hidden);
	}
}
