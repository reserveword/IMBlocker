package io.github.reserveword.imblocker.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.Window;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;
import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.PreeditEvent;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
	@Inject(method = "preeditCallback", at = @At("HEAD"), cancellable = true)
	private void overwritePreeditCallback(long handle, PreeditEvent event, CallbackInfo ci) {
		if(handle == Minecraft.getInstance().getWindow().handle()) {
			UniversalIMEPreeditOverlay.getInstance().preeditContentUpdated(event);
			if(IMBlockerConfig.INSTANCE.isIngameIMEEnabled()) {
				IMManager.updateCandidateList(handle, 0, 0, 0, 0); //Committing misses candidate callback.
			}
		}
		ci.cancel();
	}
	
	@Inject(method = "setup", at = @At("TAIL"))
	private void registerPreeditCandidateCallback(Window window, CallbackInfo ci) {
		if(IMBlockerConfig.INSTANCE.isIngameIMEEnabled()) {
			GLFW.glfwSetPreeditCandidateCallback(window.handle(), IMManager::updateCandidateList);
			IMBlockerCore.registerClientTickEvent(IMManager::updateIMEStatus); //Temporary.
		}
	}
}
