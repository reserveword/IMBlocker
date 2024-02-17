package io.github.reserveword.imblocker.asmdummy;

import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
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

//    @Override
//    public void m_94120_() {
//        IMCheckState.captureTick(this, this.m_94204_()); // canWrite
//        ++this.cursorCounter;
//    }

    @Override
    public boolean m_5534_(char codePoint, int modifiers) { // charTyped
        IMCheckState.captureNonPrintable(this, codePoint, this.m_94204_());
        if (!this.m_94204_()) {
            return false;
        } else if (SharedConstants.m_136188_(codePoint)) {
            if (this.isEnabled) {
                this.m_94164_(Character.toString(codePoint));
            }

            return true;
        } else {
            return false;
        }
    }

}
