package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.ftb.mods.ftblibrary.ui.MultilineTextBox;
import io.github.reserveword.imblocker.common.FtbMultilineTextFieldAccessor;
import net.minecraft.client.gui.EditBox;

@Mixin(value = MultilineTextBox.class, remap = false)
public abstract class FtbMultilineTextFieldAccessorImpl implements FtbMultilineTextFieldAccessor {
	
	@Shadow
	EditBox textField;
	
	@Override
	public int getCursorLineIndex() {
		return textField.getCurrentLineIndex();
	}
	
	@Override
	public int getLineBeginIndex(int lineIndex) {
		return ((SubstringAccessor) (Object) textField.getLine(lineIndex)).getBeginIndex();
	}
	
	@Override
	@Shadow
	public abstract String getText();
	
	@Override
	public int getCursor() {
		return textField.getCursor();
	}
}
