package io.github.reserveword.imblocker.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.EditBox;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {
	
	@Shadow
	private List<SubstringAccessor> lines;
	
	@Shadow
	private int cursor;
	
	@Overwrite
	public int getCurrentLineIndex() {
		for (int i = lines.size() - 1; i >= 0; i--) {
			SubstringAccessor substring = (SubstringAccessor) lines.get(i);
			if (cursor >= substring.getBeginIndex() && cursor <= substring.getEndIndex()) {
				return i;
			}
		}

		return -1;
	}
}
