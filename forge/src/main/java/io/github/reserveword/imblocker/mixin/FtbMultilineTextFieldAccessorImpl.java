package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.ftb.mods.ftblibrary.ui.MultilineTextBox;
import io.github.reserveword.imblocker.common.accessor.FtbMultilineTextFieldAccessor;
import io.github.reserveword.imblocker.common.gui.CursorInfo;
import net.minecraft.client.gui.components.MultilineTextField;

@Mixin(value = MultilineTextBox.class, remap = false)
public abstract class FtbMultilineTextFieldAccessorImpl implements FtbMultilineTextFieldAccessor {

	@Shadow
	MultilineTextField textField;
	
	@Override
	public CursorInfo getCursorInfo() {
		int cursorLineIndex = textField.getLineAtCursor();
		return new CursorInfo(true, 0/*useless*/, cursorLineIndex, 0/*useless*/, 
				((StringViewAccessor) (Object) textField.getLineView(cursorLineIndex)).getBeginIndex(), 
				textField.cursor(), textField.value());
	}
}
