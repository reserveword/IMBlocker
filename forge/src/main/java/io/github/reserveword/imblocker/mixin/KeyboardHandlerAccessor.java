package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.KeyboardHandler;

@Mixin(KeyboardHandler.class)
public interface KeyboardHandlerAccessor {
	@Invoker("charTyped")
	void invokeCharTyped(long window, int codePoint, int modifiers);
}
