package io.github.reserveword.imblocker.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.components.MultiLineEditBox;

@Mixin(MultiLineEditBox.class)
public abstract class MultiLineEditBoxMixin extends AbstractWidgetMixin {
	
	@Override
	public boolean isWidgetEditable() {
		return true;
	}
}
