package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.ldtteam.blockui.views.View;

@Mixin(View.class)
public abstract class BlockUIViewMixin extends BlockUIPaneMixin {
	@Shadow protected int padding;
}
