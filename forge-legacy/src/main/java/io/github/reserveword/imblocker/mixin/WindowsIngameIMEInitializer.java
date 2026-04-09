package io.github.reserveword.imblocker.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.IMManager;
import net.minecraft.client.MainWindow;
import net.minecraft.client.renderer.IWindowEventListener;
import net.minecraft.client.renderer.MonitorHandler;
import net.minecraft.client.renderer.ScreenSize;

@Mixin(MainWindow.class)
public class WindowsIngameIMEInitializer {
	@Shadow
	private long window;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	public void initializeIngameIME(IWindowEventListener eventListener, MonitorHandler monitorHandler,
			ScreenSize screenSize, @Nullable String string1, String string2, CallbackInfo ci) {
		IMManager.initializeIngameIME(window);
	}
}
