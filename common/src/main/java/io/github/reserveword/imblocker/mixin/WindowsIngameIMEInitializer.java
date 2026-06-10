package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.MonitorManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.systems.GpuBackend;

import io.github.reserveword.imblocker.common.IMManager;

@Mixin(Window.class)
public abstract class WindowsIngameIMEInitializer {
	@Shadow
	private long handle;
	
	@Inject(method = "<init>(Lcom/mojang/blaze3d/platform/WindowEventHandler;Lcom/mojang/blaze3d/platform/DisplayData;Ljava/lang/String;Ljava/lang/String;Lcom/mojang/blaze3d/systems/GpuBackend;)V", 
			at = @At("TAIL"), require = 0)
	private void initializeIngameIME(WindowEventHandler eventHandler, DisplayData displayData,
			String fullscreenVideoModeString, String title, GpuBackend backend, CallbackInfo ci) {
		IMManager.initializeIngameIME(handle);
	}
	
	@Inject(method = "<init>(Lcom/mojang/blaze3d/platform/WindowEventHandler;Lcom/mojang/blaze3d/platform/DisplayData;Ljava/lang/String;ZLjava/lang/String;Lcom/mojang/blaze3d/platform/MonitorManager;Lcom/mojang/blaze3d/systems/GpuBackend;)V",
			at = @At("TAIL"), require = 0)
	private void initializeIngameIME(WindowEventHandler eventHandler, DisplayData displayData,
			String fullscreenVideoModeString, boolean exclusiveFullscreen, String title,
			MonitorManager monitorManager, GpuBackend backend, CallbackInfo ci) {
		IMManager.initializeIngameIME(handle);
	}
}
