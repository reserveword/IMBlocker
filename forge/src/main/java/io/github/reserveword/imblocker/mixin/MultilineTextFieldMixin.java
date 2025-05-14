package io.github.reserveword.imblocker.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.components.MultilineTextField;

@Mixin(MultilineTextField.class)
public interface MultilineTextFieldMixin {
	
	@Accessor("displayLines")
	public List<StringViewMixin> getDisplayLines();
}
