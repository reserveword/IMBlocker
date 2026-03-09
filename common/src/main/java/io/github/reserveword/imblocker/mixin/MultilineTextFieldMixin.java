package io.github.reserveword.imblocker.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.components.MultilineTextField;

@Mixin(MultilineTextField.class)
public abstract class MultilineTextFieldMixin {

	@Shadow
	private List<StringViewAccessor> displayLines;
	
	@Shadow
	private int cursor;
	
	@Overwrite
	public int getLineAtCursor() {
		for (int i = displayLines.size() - 1; i >= 0; i--) {
			StringViewAccessor substring = (StringViewAccessor) displayLines.get(i);
			if (cursor >= substring.getBeginIndex() && cursor <= substring.getEndIndex()) {
				return i;
			}
		}

		return -1;
	}
}
