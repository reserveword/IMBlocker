package io.github.reserveword.imblocker.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.EditBox;

@Mixin(EditBox.class)
public interface EditBoxAccessor {

	@Accessor("lines")
	List<SubstringAccessor> getLines();
}
