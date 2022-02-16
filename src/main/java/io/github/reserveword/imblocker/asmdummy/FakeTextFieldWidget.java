package io.github.reserveword.imblocker.asmdummy;

import io.github.reserveword.imblocker.IMBlocker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

/**
 * We use this dummy class to help us decide how we modify EditBox.charTyped
 */
public class FakeTextFieldWidget extends EditBox {

    private boolean isEnabled = true;

    public FakeTextFieldWidget(Font fontRenderer, int x, int y, int width, int height, @Nullable EditBox inputWidget, Component title) {
        super(fontRenderer, x, y, width, height, inputWidget, title);
        IMBlocker.RegistryEvents.collectTextField(this);
    }


    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        if (!this.m_94204_()) {
            return false;
        } else if (SharedConstants.m_136188_(p_charTyped_1_)) {
            if (this.isEnabled) {
                this.m_94164_(Character.toString(p_charTyped_1_));
            }

            return true;
        } else {
            return false;
        }
    }
}
