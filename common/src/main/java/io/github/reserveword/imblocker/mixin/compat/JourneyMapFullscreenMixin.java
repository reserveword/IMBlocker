package io.github.reserveword.imblocker.mixin.compat;

import java.lang.reflect.Method;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.ReflectionUtil;

@Mixin(targets = "journeymap.client.ui.fullscreen.Fullscreen", remap = false)
public abstract class JourneyMapFullscreenMixin {
	
	private static final Method MAPCHAT_KEYPRESSED_METHOD = ReflectionUtil
			.findMethod(getChatScreenClass(),
					new String[] {"keyPressed", "method_25404", "m_7933_", "func_231046_a_"}, 
					new Class[] {int.class, int.class, int.class});
	
	@Shadow
	public abstract boolean isChatOpen();

	@Inject(method = {"keyPressed", "method_25404", "m_7933_", "func_231046_a_"}, at = @At("HEAD"), cancellable = true)
	public void checkFocusTracking(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if(IMBlockerCore.isTrackingFocus && isChatOpen()) {
			//Special process here due to its ridiculous focus management.
			Object mapChat = ReflectionUtil.getFieldValue(getClass(), this, null, "chat");
			try {
				cir.setReturnValue((Boolean) MAPCHAT_KEYPRESSED_METHOD
						.invoke(mapChat, keyCode, scanCode, modifiers));
			} catch (Throwable e) {}
		}
	}
	
	private static Class<?> getChatScreenClass() {
		try {
			return Class.forName("journeymap.client.ui.fullscreen.MapChat").getSuperclass();
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
}
