package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.widget.EditBoxWidget;

@Mixin(EditBoxWidget.class)
public abstract class EditBoxWidgetMixin extends ClickableWidgetMixin {

	@Override
	public boolean isWidgetEditable() {
		return true;
	}
}
