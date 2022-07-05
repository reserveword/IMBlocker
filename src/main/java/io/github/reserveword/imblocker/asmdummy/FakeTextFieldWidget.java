package io.github.reserveword.imblocker.asmdummy;

import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.SharedConstants;
import net.minecraft.network.chat.Component;

/**
 * We use this dummy class to help us decide how we modify EditBox.tick
 */
public class FakeTextFieldWidget extends EditBox {

    private int cursorCounter;
    private boolean isEnabled;

    public FakeTextFieldWidget(Font fontRenderer, int x, int y, int width, int height, Component title) {
        super(fontRenderer, x, y, width, height, title);
    }

    @Override
    public void m_94120_() {
        IMCheckState.captureTick(this, this.canWrite());
        ++this.cursorCounter;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        IMCheckState.captureNonPrintable(this, codePoint, this.canWrite());
        if (!this.canWrite()) {
            return false;
        } else if (SharedConstants.isAllowedCharacter(codePoint)) {
            if (this.isEnabled) {
                this.writeText(Character.toString(codePoint));
            }

            return true;
        } else {
            return false;
        }
    }

}
