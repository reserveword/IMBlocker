package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import io.github.reserveword.imblocker.common.ReflectionUtil;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.components.SearchTextFieldComponent;
import net.minecraft.client.gui.IGuiEventListener;

@Pseudo
@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options.client.gui.frame.AbstractFrame", remap = false)
public abstract class RSOAbstractFrameMixin {
	
	@Shadow
	private IGuiEventListener focused;

	@Overwrite
	public void func_231035_a_(IGuiEventListener focused) {
		if (this.focused != focused) {
			if(this.focused != null && SearchTextFieldComponent.class.isInstance(this.focused)) {
				ReflectionUtil.invokeMethod(SearchTextFieldComponent.class, this.focused, null, 
						"setFocused", new Class[] {boolean.class}, false);
			}
			
			this.focused = focused;
		}
	}
}
