package io.github.reserveword.imblocker.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(targets = "androidx.compose.ui.text.TextLayoutResult", remap = false)
public interface ComposeTextLayoutResultAccessor {
	@Accessor
	long getSize();
}
