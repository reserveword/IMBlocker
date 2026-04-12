package io.github.reserveword.imblocker.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;

import io.github.reserveword.imblocker.common.IMManager;

@Mixin(Window.class)
public abstract class WindowsIngameIMEInitializer {
	@Shadow
	private long window;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	public void initializeIngameIME(WindowEventHandler eventHandler, ScreenManager screenManager, 
			DisplayData displayData, @Nullable String string1, String string2, CallbackInfo ci) {
		IMManager.initializeIngameIME(window);
	}
}
