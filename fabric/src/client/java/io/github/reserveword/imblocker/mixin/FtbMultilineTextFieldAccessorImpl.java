package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.ftb.mods.ftblibrary.ui.MultilineTextBox;
import io.github.reserveword.imblocker.common.accessor.FtbMultilineTextFieldAccessor;
import io.github.reserveword.imblocker.common.gui.MultilineCursorInfo;
import net.minecraft.client.gui.EditBox;

@Mixin(value = MultilineTextBox.class, remap = false)
public abstract class FtbMultilineTextFieldAccessorImpl implements FtbMultilineTextFieldAccessor {
	
	@Shadow
	EditBox textField;
	
	@Override
	public MultilineCursorInfo getCursorInfo(double scrollY) {
		int cursorLineIndex = textField.getCurrentLineIndex();
		return new MultilineCursorInfo(cursorLineIndex, scrollY, 
				((SubstringAccessor) (Object) textField.getLine(cursorLineIndex)).getBeginIndex(), 
				textField.getCursor(), textField.getText());
	}
}
