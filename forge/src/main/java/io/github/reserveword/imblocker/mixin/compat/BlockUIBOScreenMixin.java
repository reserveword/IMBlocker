package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.ldtteam.blockui.BOScreen;

@Mixin(BOScreen.class)
public abstract class BlockUIBOScreenMixin {
	@Shadow protected double renderScale;
	@Shadow protected double x;
	@Shadow protected double y;
}
