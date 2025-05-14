package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.widget.ScrollableWidget;

@Mixin(ScrollableWidget.class)
public abstract class ScrollableWidgetMixin extends ClickableWidgetMixin {
	
	@Shadow
	protected abstract boolean isVisible(int top, int bottom);
	
	@Shadow
	protected abstract double getScrollY();
}
