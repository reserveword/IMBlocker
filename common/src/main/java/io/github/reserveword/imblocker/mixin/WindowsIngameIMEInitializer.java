package io.github.reserveword.imblocker.mixin;

import java.io.File;
import java.io.FileReader;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;
import net.minecraft.client.Minecraft;

@Mixin(Window.class)
public abstract class WindowsIngameIMEInitializer {
	@Shadow
	private long handle;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	public void initializeIngameIME(WindowEventHandler windowEventHandler, ScreenManager screenManager, 
			DisplayData displayData, @Nullable String string, String string2, CallbackInfo ci) {
		try {
			File imblockConfigFile = new File(Minecraft.getInstance().gameDirectory, "config/imblocker.json");
			JsonObject imblockConfigRoot = JsonParser.parseReader(new FileReader(imblockConfigFile)).getAsJsonObject();
			JsonObject windowsCompatSettings = imblockConfigRoot.getAsJsonObject("windowsCompatibilitySettings");
			boolean enableIngameIME = windowsCompatSettings.get("enableIngameIME").getAsBoolean();
			if(enableIngameIME) {
				IMManager.initializeIngameIME(handle);
			}
		} catch (Throwable e) {
			IMBlockerCore.LOGGER.error("[IMBlocker] Failed to get enableIngameIME config value: " + e);
		}
	}
}
