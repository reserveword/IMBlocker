package io.github.reserveword.imblocker.mixin;

import java.io.File;
import java.io.FileReader;
import java.util.function.LongSupplier;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.BackendOptions;
import com.mojang.blaze3d.platform.GLX;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import net.minecraft.client.Minecraft;

@Mixin(GLX.class)
public abstract class WindowsIngameIMEInitializer {
	@Inject(method = "_initGlfw", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwInit()Z"))
	private static void applyIMEInitHint(BackendOptions options, CallbackInfoReturnable<LongSupplier> cir) {
		try {
			File imblockConfigFile = new File(Minecraft.getInstance().gameDirectory, "config/imblocker.json");
			JsonObject imblockConfigRoot = JsonParser.parseReader(new FileReader(imblockConfigFile)).getAsJsonObject();
			JsonObject windowsCompatSettings = imblockConfigRoot.getAsJsonObject("windowsCompatibilitySettings");
			boolean enableIngameIME = windowsCompatSettings.get("enableIngameIME").getAsBoolean();
			if(enableIngameIME) {
				GLFW.glfwInitHint(GLFW.GLFW_MANAGE_PREEDIT_CANDIDATE, 1);
			}
		} catch (Throwable e) {
			IMBlockerCore.LOGGER.error("[IMBlocker] Failed to get enableIngameIME config value: " + e);
		}
	}
}
