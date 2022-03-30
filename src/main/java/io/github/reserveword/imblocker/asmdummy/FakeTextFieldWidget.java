package io.github.reserveword.imblocker.asmdummy;

import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * We use this dummy class to help us decide how we modify EditBox.charTyped
 */
public class FakeTextFieldWidget extends EditBox {

    private int cursorCounter;

    public FakeTextFieldWidget(Font fontRenderer, int x, int y, int width, int height, Component title) {
        super(fontRenderer, x, y, width, height, title);
    }

    @Override
    public void tick() {
        IMCheckState.imTick(this);
        ++this.cursorCounter;
    }

}
