package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.LinuxKeyCallbackMonitor;
import net.minecraft.client.KeyboardListener;

@Mixin(KeyboardListener.class)
public abstract class LinuxKeyboardPatch {
	@Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
	private void onKeyInvoked(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		char c = (char) key;
		if(action != 0) {
			if(c >= 'A' && c <= 'Z') {
				LinuxKeyCallbackMonitor.glfwPrintableKeyPressed();
			}else if(key == 256) {
				LinuxKeyCallbackMonitor.resetConsistency();
			}
		}
		if((c < 'A' || c > 'Z') && !LinuxKeyCallbackMonitor.isKeyConsistentWithChar()) {
			ci.cancel();
		}
	}
	
	@Inject(method = "charTyped", at = @At("HEAD"))
	private void onCharInvoked(long window, int codePoint, int modifiers, CallbackInfo ci) {
		LinuxKeyCallbackMonitor.glfwCharTyped();
	}
}
