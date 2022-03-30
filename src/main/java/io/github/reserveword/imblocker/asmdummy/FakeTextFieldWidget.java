package io.github.reserveword.imblocker.asmdummy;

import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;

/**
 * We use this dummy class to help us decide how we modify TextFieldWidget.charTyped
 */
public class FakeTextFieldWidget extends TextFieldWidget {

    private int cursorCounter;

    public FakeTextFieldWidget(FontRenderer fontRenderer, int x, int y, int width, int height, ITextComponent title) {
        super(fontRenderer, x, y, width, height, title);
    }

    @Override
    public void tick() {
        IMCheckState.imTick(this);
        ++this.cursorCounter;
    }

}
