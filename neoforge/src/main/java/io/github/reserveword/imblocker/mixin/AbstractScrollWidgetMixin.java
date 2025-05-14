package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.components.AbstractScrollWidget;

@Mixin(AbstractScrollWidget.class)
public abstract class AbstractScrollWidgetMixin extends AbstractWidgetMixin {
	
	@Shadow
	protected abstract boolean withinContentAreaTopBottom(int top, int bottom);
}
