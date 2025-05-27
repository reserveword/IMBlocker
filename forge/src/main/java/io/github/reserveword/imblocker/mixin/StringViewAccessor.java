package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.gui.components.MultilineTextField$StringView")
public interface StringViewAccessor {
	
	@Accessor("beginIndex")
	int getBeginIndex();
	
	@Accessor("endIndex")
	int getEndIndex();
}
