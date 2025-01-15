package io.github.reserveword.imblocker.mixin.fabric;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Element;

@Mixin(AbstractParentElement.class)
public class AbstractParentElementMixin {
	
	@Shadow
	@Nullable
	private Element focused;
	
	@Overwrite
	public void setFocused(@Nullable Element focused) {
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
