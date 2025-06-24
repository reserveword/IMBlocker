package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.Keyboard;

@Mixin(Keyboard.class)
public interface KeyboardAccessor {
	@Invoker("onChar")
	void invokeOnChar(long window, int codePoint, int modifiers);
}
