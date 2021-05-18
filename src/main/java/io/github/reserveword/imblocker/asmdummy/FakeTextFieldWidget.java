package io.github.reserveword.imblocker.asmdummy;

import io.github.reserveword.imblocker.IMBlocker;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.SharedConstants;

/**
 * We use this dummy class to help us decide how we modify TextFieldWidget.charTyped
 */
public class FakeTextFieldWidget extends TextFieldWidget {

    private boolean isEnabled = true;

    public FakeTextFieldWidget(FontRenderer fontIn, int p_i51137_2_, int p_i51137_3_, int p_i51137_4_, int p_i51137_5_, String msg) {
        super(fontIn, p_i51137_2_, p_i51137_3_, p_i51137_4_, p_i51137_5_, msg);
    }

    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        IMBlocker.TextFieldConfirmEvent.fireEvent(this, this.isEnabled);
        if (!this.func_212955_f()) {
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
