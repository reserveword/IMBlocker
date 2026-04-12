package io.github.reserveword.imblocker.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.Window;

@Mixin(Window.class)
public abstract class WindowsIngameIMEInitializer {
	@Shadow
	private long handle;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void initializeIngameIME(WindowEventHandler eventHandler, MonitorTracker monitorTracker,
			WindowSettings settings, @Nullable String videoMode, String title, CallbackInfo ci) {
		IMManager.initializeIngameIME(handle);
	}
}
