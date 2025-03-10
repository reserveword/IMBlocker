package io.github.reserveword.imblocker.mixin.neoforge;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

@Mixin(AbstractContainerEventHandler.class)
public class AbstractContainerEventHandlerMixin {
	
	@Shadow
	@Nullable
	private GuiEventListener focused;
	
	@Overwrite
	public void setFocused(@Nullable GuiEventListener focused) {
		if(this.focused != focused) {
			if (this.focused != null) {
				this.focused.setFocused(false);
			}

			if (focused != null) {
				focused.setFocused(true);
			}

			this.focused = focused;
		}
	}
}
