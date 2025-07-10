package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.ldtteam.blockui.views.View;

@Mixin(value = View.class, remap = false)
public interface BlockUIViewAccessor {
	@Accessor("padding")
	int getPadding();
}
