package io.github.reserveword.imblocker.asmdummy;

import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;

/**
 * We use this dummy class to help us decide how we modify TextFieldWidget.charTyped
 */
public class FakeTextFieldWidget extends TextFieldWidget {

    private int cursorCounter;
    private boolean isEnabled;

    public FakeTextFieldWidget(FontRenderer fontRenderer, int x, int y, int width, int height, ITextComponent title) {
        super(fontRenderer, x, y, width, height, title);
    }

    @Override
    public void tick() {
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
