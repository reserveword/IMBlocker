package io.github.reserveword.imblocker.mixin;

import java.io.File;
import java.io.FileReader;

import org.lwjgl.sdl.SDLHints;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.SDLEventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.util.TimeSource.NanoTimeSource;

@Mixin(RenderSystem.class)
public abstract class SDLIngameIMEInitializer {
	@Inject(method = "initBackendSystem", at = @At(value = "INVOKE", target = 
			"Lorg/lwjgl/sdl/SDLInit;SDL_Init(I)Z"))
	private static void initializeIngameIME(CallbackInfoReturnable<NanoTimeSource> cir) {
		try {
			File imblockConfigFile = new File(Minecraft.getInstance().gameDirectory, "config/imblocker.json");
			JsonObject imblockConfigRoot = JsonParser.parseReader(new FileReader(imblockConfigFile)).getAsJsonObject();
			JsonObject advanceSettings = imblockConfigRoot.getAsJsonObject("advanceSettings");
			boolean enableIngameIME = advanceSettings.get("enableIngameIME").getAsBoolean();
			if(enableIngameIME) {
				SDLHints.SDL_SetHint(SDLHints.SDL_HINT_IME_IMPLEMENTED_UI, "composition,candidates");
				SDLEventListener.enableCandidateEvent();
			}
		} catch (Throwable e) {
			IMBlockerCore.LOGGER.error("[IMBlocker] Failed to get enableIngameIME config value: " + e);
		}
	}
}
