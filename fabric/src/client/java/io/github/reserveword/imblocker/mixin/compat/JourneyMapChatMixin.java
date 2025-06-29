package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.mixin.ChatScreenMixin;
import net.minecraft.client.gui.ParentElement;

@Pseudo
@Mixin(targets = "journeymap.client.ui.fullscreen.MapChat", remap = false)
public abstract class JourneyMapChatMixin extends ChatScreenMixin {
	
	@Shadow
	protected boolean hidden;
	
	@Override
	protected void initChatState(CallbackInfo ci) {
		super.initChatState(ci);
		chatField.setVisible(!this.hidden);
	}
	
	@Inject(method = "close", at = @At("TAIL"))
	public void visibilityChanged(CallbackInfo ci) {
		chatField.setVisible(false);
	}
	
	@Inject(method = "setHidden", at = @At("TAIL"))
	public void visibilityChanged(boolean hidden, CallbackInfo ci) {
		chatField.setVisible(!this.hidden);
		if(IMBlockerCore.isGameVersionReached(762/*1.19.4*/)) {
			((ParentElement) this).setFocused(chatField);
		}
	}
}
