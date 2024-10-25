package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import io.github.cottonmc.cotton.gui.widget.WTextField;

@Pseudo
@Mixin(value = WTextField.class, remap = false)
public abstract class CottonTextFieldMixin extends CottonWidgetMixin {
	@Shadow
	public abstract boolean isEditable();
}
