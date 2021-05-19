package io.github.reserveword.imblocker.asmdummy;

import io.github.reserveword.imblocker.IMBlocker;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

/**
 * We use this dummy class to help us decide how we modify TextFieldWidget.charTyped
 */
public class FakeTextFieldWidget extends TextFieldWidget {

    private boolean isEnabled = true;

    public FakeTextFieldWidget(FontRenderer fontRenderer, int x, int y, int width, int height, @Nullable TextFieldWidget inputWidget, ITextComponent title) {
        super(fontRenderer, x, y, width, height, inputWidget, title);
        IMBlocker.RegistryEvents.collectTextField(this);
    }


    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        if (!this.canWrite()) {
            return false;
        } else if (SharedConstants.isAllowedCharacter(p_charTyped_1_)) {
            if (this.isEnabled) {
                this.writeText(Character.toString(p_charTyped_1_));
            }

            return true;
        } else {
            return false;
        }
    }
}
