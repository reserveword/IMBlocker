package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.ldtteam.blockui.BOScreen;

@Mixin(value = BOScreen.class, remap = false)
public interface BlockUIBOScreenAccessor {
	@Accessor("x")
	double getX();
	
	@Accessor("y")
	double getY();
}
