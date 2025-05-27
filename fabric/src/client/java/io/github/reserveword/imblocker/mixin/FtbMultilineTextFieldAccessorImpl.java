package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.ftb.mods.ftblibrary.ui.MultilineTextBox;
import io.github.reserveword.imblocker.common.gui.CursorInfo;
import io.github.reserveword.imblocker.common.gui.FtbMultilineTextFieldAccessor;
import net.minecraft.client.gui.EditBox;

@Mixin(value = MultilineTextBox.class, remap = false)
public abstract class FtbMultilineTextFieldAccessorImpl implements FtbMultilineTextFieldAccessor {
	
	@Shadow
	EditBox textField;
	
	@Override
	public CursorInfo getCursorInfo() {
		int cursorLineIndex = textField.getCurrentLineIndex();
		return new CursorInfo(true, 0/*useless*/, cursorLineIndex, 0/*useless*/, 
				((SubstringAccessor) (Object) textField.getLine(cursorLineIndex)).getBeginIndex(), 
				textField.getCursor(), textField.getText());
	}
}
